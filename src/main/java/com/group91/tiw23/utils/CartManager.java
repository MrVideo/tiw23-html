package com.group91.tiw23.utils;

import com.group91.tiw23.beans.ShipmentPolicyBean;
import com.group91.tiw23.daos.InventoryDAO;
import com.group91.tiw23.daos.ProductDAO;
import com.group91.tiw23.daos.ShipmentPolicyDAO;
import com.group91.tiw23.daos.SupplierDAO;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mario Merlo
 */
public class CartManager {
    public static Cookie saveCart(HashMap<Integer, ArrayList<CartEntry>> cart) {
        Cookie cookie;

        if(cart != null && !cart.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            for(Map.Entry<Integer, ArrayList<CartEntry>> entry : cart.entrySet()) {
                ArrayList<CartEntry> supplierItems = entry.getValue();

                for(CartEntry item : supplierItems) {
                    stringBuilder.append(item.toString());
                }
            }
            cookie = new Cookie("cart", stringBuilder.toString());
            cookie.setMaxAge(86400);
        } else {
            cookie = new Cookie("cart", "");
            cookie.setMaxAge(86400);
        }

        return cookie;
    }

    public static HashMap<Integer, ArrayList<CartEntry>> rebuildCart(Cookie cookie, HttpSession session, Connection connection) {
        ArrayList<CartEntry> cartEntries = new ArrayList<>();

        String cartString = cookie.getValue();
        String[] splits = cartString.split("\\.");

        for(String split : splits) {
            CartEntry entry = new CartEntry(split);
            cartEntries.add(entry);
        }

        ProductDAO productDAO = new ProductDAO(connection);
        SupplierDAO supplierDAO = new SupplierDAO(connection);
        InventoryDAO inventoryDAO = new InventoryDAO(connection);

        HashMap<Integer, ArrayList<CartEntry>> cart = new HashMap<>();

        HashMap<Integer, Float> totalValues, shippingCosts;

        try {
            for(CartEntry entry : cartEntries) {
                int productId = entry.getProductBean().getProductId();
                int supplierId = entry.getSupplierBean().getSupplierId();

                entry.setProductBean(productDAO.getDetailsFromId(productId));
                entry.getProductBean().setPrice(inventoryDAO.getPrice(productId, supplierId));

                entry.setSupplierBean(supplierDAO.getSupplierById(supplierId));

                ArrayList<CartEntry> supplierItems;

                if(cart.get(supplierId) == null) {
                    supplierItems = new ArrayList<>();
                    supplierItems.add(entry);
                    cart.put(supplierId, supplierItems);
                } else {
                    supplierItems = cart.get(supplierId);
                    supplierItems.add(entry);
                }
            }

            totalValues = new HashMap<>();
            shippingCosts = new HashMap<>();

            computeTotals(cart, totalValues, shippingCosts, connection);

            // Add name mapping for suppliers to user session
            session.setAttribute("nameMap", supplierDAO.getIdToNameMapping());
        } catch (SQLException e) {
            return null;
        }

        // Add total and shipping to session
        session.setAttribute("totals", totalValues);
        session.setAttribute("shipping", shippingCosts);

        return cart;
    }

    public static void computeTotals(HashMap<Integer, ArrayList<CartEntry>> cart, HashMap<Integer, Float> itemTotal, HashMap<Integer, Float> shipping, Connection connection) throws SQLException {
        ShipmentPolicyDAO shipmentPolicyDAO = new ShipmentPolicyDAO(connection);

        for(Map.Entry<Integer, ArrayList<CartEntry>> entry : cart.entrySet()) {
            float totalValue = 0F;
            int totalQuantity = 0;

            for(CartEntry productEntry : entry.getValue()) {
                totalValue += productEntry.getQuantity() * productEntry.getProductBean().getPrice();
                totalQuantity += productEntry.getQuantity();
            }

            itemTotal.put(entry.getKey(), totalValue);

            float freeShippingThreshold = entry.getValue().get(0).getSupplierBean().getFreeShippingThreshold();

            if(totalValue > freeShippingThreshold)
                shipping.put(entry.getKey(), 0F);
            else {
                List<ShipmentPolicyBean> policies = shipmentPolicyDAO.getPoliciesBySupplier(entry.getKey());

                for(ShipmentPolicyBean policy : policies) {
                    if(totalQuantity >= policy.getMinItems() && (totalQuantity <= policy.getMaxItems() || policy.getMaxItems() == 0))
                        shipping.put(entry.getKey(), policy.getShipmentCost());
                }
            }
        }
    }
}
