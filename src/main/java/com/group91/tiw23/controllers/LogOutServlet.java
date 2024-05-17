package com.group91.tiw23.controllers;

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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Mario Merlo
 */
@WebServlet("/logout")
public class LogOutServlet extends HttpServlet {
    private TemplateEngine templateEngine;
    private ServletContext context;

    @Override
    public void init() {
        context = getServletContext();

        // Thymeleaf initialization
        templateEngine = ServletInitializer.templateEngineInit(context);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        HashMap<Integer, ArrayList<CartEntry>> cart = (HashMap<Integer, ArrayList<CartEntry>>) session.getAttribute("cart");

        Cookie cartCookie = CartManager.saveCart(cart);

        response.addCookie(cartCookie);

        session.invalidate();

        WebContext ctx = new WebContext(request, response, context, request.getLocale());
        templateEngine.process("logout.html", ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}