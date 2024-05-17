package com.group91.tiw23.controllers;

import com.group91.tiw23.beans.ProductBean;
import com.group91.tiw23.daos.ProductDAO;
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
import java.util.List;

/**
 * @author Mario Merlo
 */
@WebServlet("/search")
public class QueryServlet extends HttpServlet {
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
        String keyword = request.getParameter("query");

        ProductDAO dao = new ProductDAO(connection);

        List<ProductBean> searchResult = null;
        try {
            searchResult = dao.productSearch(keyword);
        } catch (SQLException e) {
            // Redirect to server error page if database connection fails
            response.sendRedirect(request.getContextPath() + "/error");
        }

        HttpSession session = request.getSession(false);

        session.setAttribute("searchResult", searchResult);
        session.setAttribute("keyword", keyword);
        response.sendRedirect(request.getContextPath() + "/results");
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