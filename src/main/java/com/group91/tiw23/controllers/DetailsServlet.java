package com.group91.tiw23.controllers;

import com.group91.tiw23.beans.ProductBean;
import com.group91.tiw23.beans.SupplierBean;
import com.group91.tiw23.beans.UserBean;
import com.group91.tiw23.daos.*;
import com.group91.tiw23.utils.CartEntry;
import com.group91.tiw23.utils.NullChecker;
import com.group91.tiw23.utils.ServletInitializer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
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
import java.util.List;

/**
 * @author Mario Merlo
 */
@WebServlet("/results/details")
public class DetailsServlet extends HttpServlet {
    private TemplateEngine templateEngine;
    private ServletContext context;
    private Connection connection;
    @Override
    public void init() {
        context = getServletContext();

        // Thymeleaf initialization
        templateEngine = ServletInitializer.templateEngineInit(context);

        // Database connection initialization
        try {
            connection = ServletInitializer.databaseConnectionInit(context);
        } catch (ClassNotFoundException e) {
            System.err.println("DB Driver not found.");
        } catch (SQLException e) {
            System.err.println("Unable to connect to DB.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        session.setAttribute("showDetails", true);

        String idParameter = request.getParameter("productId");

        if(NullChecker.voidCheck(idParameter)) {
            try {
                int productId = Integer.parseInt(idParameter);

                // Get item details
                ProductDAO productDAO = new ProductDAO(connection);
                ProductBean productDetails = productDAO.getDetailsFromId(productId);

                session.setAttribute("selected", productDetails);

                // Get supplier list
                SupplierDAO supplierDAO = new SupplierDAO(connection);
                List<SupplierBean> suppliers = supplierDAO.getSuppliersByProductId(productId);

                // Add shipping policies and price information to supplier list
                ShipmentPolicyDAO shipmentPolicyDAO = new ShipmentPolicyDAO(connection);
                InventoryDAO inventoryDAO = new InventoryDAO(connection);
                for (SupplierBean supplier : suppliers) {
                    supplier.setShippingPolicies(shipmentPolicyDAO.getPoliciesBySupplier(supplier.getSupplierId()));
                    supplier.setPrice(inventoryDAO.getPrice(productId, supplier.getSupplierId()));
                }

                // Add total quantity and total value of cart items to supplier list
                HashMap<Integer, ArrayList<CartEntry>> cart = (HashMap<Integer, ArrayList<CartEntry>>) session.getAttribute("cart");

                for (SupplierBean supplier : suppliers) {
                    int totalQuantity = 0;
                    float totalValue = 0;
                    ArrayList<CartEntry> supplierItems = cart.get(supplier.getSupplierId());
                    if (supplierItems != null) {
                        for (CartEntry entry : supplierItems) {
                            totalQuantity += entry.getQuantity();
                            totalValue += inventoryDAO.getPrice(entry.getProductBean().getProductId(), supplier.getSupplierId()) * entry.getQuantity();
                        }
                    }
                    supplier.setTotalCartQuantity(totalQuantity);
                    supplier.setTotalCartValue(totalValue);
                }

                session.setAttribute("suppliers", suppliers);

                // Update recently viewed items
                ViewsDAO viewsDAO = new ViewsDAO(connection);
                UserBean userData = (UserBean) session.getAttribute("userData");
                viewsDAO.updateLastViewedDate(userData.getUserId(), productId);
            } catch (SQLException | NumberFormatException e) {
                // Redirect to server error page
                response.sendRedirect(request.getContextPath() + "/error");
            }
        } else response.sendRedirect(request.getContextPath() + "/error");

        // Repopulate page with the new information
        WebContext webContext = new WebContext(request, response, context, response.getLocale());
        templateEngine.process("results.html", webContext, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
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