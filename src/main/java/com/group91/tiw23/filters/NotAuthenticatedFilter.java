package com.group91.tiw23.filters;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Mario Merlo
 */
public class NotAuthenticatedFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        HttpSession session = httpServletRequest.getSession(false);

        if(session == null)
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login-error");
        else
            filterChain.doFilter(servletRequest, servletResponse);
    }
}
