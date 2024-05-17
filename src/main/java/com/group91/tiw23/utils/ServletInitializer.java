package com.group91.tiw23.utils;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Mario Merlo
 */
public class ServletInitializer {
    public static TemplateEngine templateEngineInit(ServletContext context) {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
        return templateEngine;
    }

    public static Connection databaseConnectionInit(ServletContext servletContext) throws ClassNotFoundException, SQLException {
        String dbUrl = servletContext.getInitParameter("dbUrl");
        String dbUser = servletContext.getInitParameter("dbUser");
        String dbPassword = servletContext.getInitParameter("dbPassword");
        String dbDriver = servletContext.getInitParameter("dbDriver");

        Class.forName(dbDriver);
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}
