package com.shivasiva.student.management.system.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        if ("ROLE_ADMIN".equals(role)) {
            response.sendRedirect("/admin/dashboard");
        } else if ("ROLE_STUDENT".equals(role)) {
            response.sendRedirect("/student/dashboard");
        } else {
            response.sendRedirect("/");
        }
    }
}
