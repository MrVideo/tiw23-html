package com.group91.tiw23.controllers;

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

/**
 * @author Mario Merlo
 */
@WebServlet("/results")
public class RedirectResultsServlet extends HttpServlet {
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

        session.setAttribute("showDetails", false);
        WebContext webContext = new WebContext(request, response, context, response.getLocale());
        templateEngine.process("results.html", webContext, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}