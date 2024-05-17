package com.group91.tiw23.controllers;

import com.group91.tiw23.beans.UserBean;
import com.group91.tiw23.daos.UserDAO;
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

/**
 * @author Mario Merlo
 */
@WebServlet("/login")
public class LoginFormSendServlet extends HttpServlet {
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
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if(NullChecker.voidCheck(email) && NullChecker.voidCheck(password)) {
            UserDAO dao = new UserDAO(connection);

            try {
                if(dao.login(email, password)) {
                    UserBean bean = dao.getUserDataFromEmail(email);
                    HttpSession session = request.getSession(true);
                    session.setAttribute("userData", bean);
                    response.sendRedirect(request.getContextPath() + "/home");
                } else response.sendRedirect(request.getContextPath() + "/login-error");
            } catch (SQLException e) {
                // Redirect to server error page if database connection fails
                response.sendRedirect(request.getContextPath() + "/error");
            }
        } else response.sendRedirect(request.getContextPath() + "/login-error");
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