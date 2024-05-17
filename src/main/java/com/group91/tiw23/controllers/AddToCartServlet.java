package com.group91.tiw23.controllers;

import com.group91.tiw23.beans.ProductBean;
import com.group91.tiw23.beans.SupplierBean;
import com.group91.tiw23.daos.InventoryDAO;
import com.group91.tiw23.daos.ProductDAO;
import com.group91.tiw23.daos.SupplierDAO;
import com.group91.tiw23.utils.CartEntry;
import com.group91.tiw23.utils.CartManager;
import com.group91.tiw23.utils.NullChecker;
import com.group91.tiw23.utils.ServletInitializer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Mario Merlo
 */
@WebServlet("/results/addToCart")
public class AddToCartServlet extends HttpServlet {
    private Connection connection;
    @Override
    public void init() {
        // Database connection initialization
        try {
            connection = ServletInitializer.databaseConnectionInit(getServletContext());
        } catch (ClassNotFoundException e) {
            System.err.println("DB Driver not found.");
        } catch (SQLException e) {
            System.err.println("Unable to connect to DB.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        String quantityParameter = request.getParameter("quantity");
        String productIdParameter = request.getParameter("productId");
        String supplierIdParameter = request.getParameter("supplierId");

        if(NullChecker.voidCheck(quantityParameter) && NullChecker.voidCheck(productIdParameter) && NullChecker.voidCheck(supplierIdParameter)) {
            try {
                int quantity = Integer.parseInt(quantityParameter);
                if(quantity > 0) {
                    int productId = Integer.parseInt(productIdParameter);
                    int supplierId = Integer.parseInt(supplierIdParameter);

                    // Retrieve information from database
                    ProductDAO productDAO = new ProductDAO(connection);
                    SupplierDAO supplierDAO = new SupplierDAO(connection);
                    InventoryDAO inventoryDAO = new InventoryDAO(connection);

                    ProductBean productBean = productDAO.getDetailsFromId(productId);
                    SupplierBean supplierBean = supplierDAO.getSupplierById(supplierId);
                    productBean.setPrice(inventoryDAO.getPrice(productId, supplierId));

                    CartEntry cartEntry = new CartEntry(productBean, supplierBean, quantity);

                    // Retrieve cart from user session
                    HashMap<Integer, ArrayList<CartEntry>> cart = (HashMap<Integer, ArrayList<CartEntry>>) session.getAttribute("cart");

                    // Get all items from current supplier from the cart
                    ArrayList<CartEntry> supplierItems = cart.get(supplierId);
                    boolean addedItem = false;

                    // If the list of items for a given supplier is empty, create a new one
                    if(supplierItems == null) {
                        supplierItems = new ArrayList<>();

                        // Add new item to the list of the supplier's items in the cart
                        supplierItems.add(cartEntry);

                        // Insert newly created list in cart
                        cart.put(supplierId, supplierItems);
                    } else {
                        for(CartEntry entry : supplierItems) {
                            // If the order already contained that item, update its quantity
                            if(entry.getProductBean().getProductId() == productId) {
                                entry.addQuantity(quantity);
                                addedItem = true;
                            }
                        }

                        // If the order did not contain that item, add it as a new one
                        if(!addedItem)
                            supplierItems.add(cartEntry);
                    }

                    // Calculate total price and shipping costs
                    HashMap<Integer, Float> totalValues = new HashMap<>();
                    HashMap<Integer, Float> shippingCosts = new HashMap<>();

                    CartManager.computeTotals(cart, totalValues, shippingCosts, connection);

                    // Add name mapping for suppliers to user session
                    session.setAttribute("nameMap", supplierDAO.getIdToNameMapping());

                    // Update the cart in the user session
                    session.setAttribute("cart", cart);

                    // Add total and shipping to session
                    session.setAttribute("totals", totalValues);
                    session.setAttribute("shipping", shippingCosts);

                    response.sendRedirect(request.getContextPath() + "/cart");
                } else response.sendRedirect(request.getContextPath() + "/cart/error");
            } catch (NumberFormatException e) {
                // Send to error page if the input quantity wasn't a number
                response.sendRedirect(request.getContextPath() + "/cart/error");
            } catch (SQLException e) {
                // Send to error page if database connection fails
                response.sendRedirect(request.getContextPath() + "/error");
            }
        } else {
            // Send to error page if any of the parameters is empty
            response.sendRedirect(request.getContextPath() + "/cart/error");
        }
    }

    @Override
    public void destroy() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("SQL Exception");
        }
    }
}