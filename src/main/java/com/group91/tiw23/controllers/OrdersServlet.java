package com.group91.tiw23.controllers;

import com.group91.tiw23.beans.OrderBean;
import com.group91.tiw23.beans.UserBean;
import com.group91.tiw23.daos.OrderDAO;
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

/**
 * @author Mario Merlo
 */
@WebServlet("/orders")
public class OrdersServlet extends HttpServlet {
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

        int userId = ((UserBean) session.getAttribute("userData")).getUserId();

        ArrayList<OrderBean> orders = null;
        try {
            OrderDAO orderDAO = new OrderDAO(connection);
            orders = orderDAO.getOrderDetailsByUserId(userId);
        } catch (SQLException e) {
            // Redirect to server error page if database connection fails
            response.sendRedirect(request.getContextPath() + "/error");
        }

        session.setAttribute("orders", orders);

        WebContext webContext = new WebContext(request, response, context, response.getLocale());
        templateEngine.process("orders.html", webContext, response.getWriter());
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