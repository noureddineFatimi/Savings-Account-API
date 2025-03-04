package com.example.config;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Set the response status to 403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Custom error message
        response.getWriter().write("You do not have the necessary permissions to access this resource.");
    }
}