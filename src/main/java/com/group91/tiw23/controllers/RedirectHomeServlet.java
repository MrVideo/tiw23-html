package com.group91.tiw23.controllers;

import com.group91.tiw23.beans.ProductBean;
import com.group91.tiw23.beans.UserBean;
import com.group91.tiw23.daos.ViewsDAO;
import com.group91.tiw23.utils.CartEntry;
import com.group91.tiw23.utils.CartManager;
import com.group91.tiw23.utils.ServletInitializer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Mario Merlo
 */
@WebServlet("/home")
public class RedirectHomeServlet extends HttpServlet {
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

        UserBean userBean = (UserBean) session.getAttribute("userData");
        ViewsDAO dao = new ViewsDAO(connection);
        List<ProductBean> products = null;
        try {
            products = dao.getRecentlyViewedItems(userBean.getUserId());
        } catch (SQLException e) {
            // Redirect to server error page if database connection fails
            response.sendRedirect(request.getContextPath() + "/error");
        }

        WebContext webContext = new WebContext(request, response, context, response.getLocale());
        session.setAttribute("products", products);

        HashMap<Integer, ArrayList<CartEntry>> cart = (HashMap<Integer, ArrayList<CartEntry>>) session.getAttribute("cart");

        if(cart == null) {
            Cookie[] cookies = request.getCookies();

            if(cookies != null)
                for(Cookie cookie : cookies)
                    if(cookie.getName().equals("cart") && !cookie.getValue().equals(""))
                        cart = CartManager.rebuildCart(cookie, session, connection);

            if(cart == null)
                cart = new HashMap<>();

            session.setAttribute("cart", cart);
        }

        templateEngine.process("home.html", webContext, response.getWriter());
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