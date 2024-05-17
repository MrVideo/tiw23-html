package com.group91.tiw23.controllers;

import com.group91.tiw23.beans.UserBean;
import com.group91.tiw23.daos.OrderDAO;
import com.group91.tiw23.utils.CartEntry;
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
@WebServlet("/cart/placeorder")
public class PlaceOrderServlet extends HttpServlet {
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

        String idParameter = request.getParameter("supplierId");

        if(NullChecker.voidCheck(idParameter)) {
            try {
                int supplierId = Integer.parseInt(idParameter);

                HashMap<Integer, ArrayList<CartEntry>> cart = (HashMap<Integer, ArrayList<CartEntry>>) session.getAttribute("cart");
                HashMap<Integer, Float> totals = (HashMap<Integer, Float>) session.getAttribute("totals");
                HashMap<Integer, Float> shipping = (HashMap<Integer, Float>) session.getAttribute("shipping");
                UserBean userData = (UserBean) session.getAttribute("userData");

                OrderDAO orderDAO = new OrderDAO(connection);
                orderDAO.placeOrder(userData.getUserId(), supplierId, cart.get(supplierId), totals.get(supplierId) + shipping.get(supplierId));

                cart.remove(supplierId);
                totals.remove(supplierId);
                shipping.remove(supplierId);

                response.sendRedirect(request.getContextPath() + "/orders");
            } catch (SQLException | NumberFormatException e) {
                // Redirect to server error page if database connection fails
                response.sendRedirect(request.getContextPath() + "/error");
            }
        } else response.sendRedirect(request.getContextPath() + "/error");
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