//package com.Bank.managementSystem.Security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.server.authorization;
////import org.springframework.security.oauth2.server.authorization.
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class OAuth2TokenFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private TokenStore tokenStore;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        final String authorizationHeader = request.getHeader("Authorization");
//
//        String token = null;
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            token = authorizationHeader.substring(7);
//        }
//
//        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            OAuth2Authentication authentication = tokenStore.readAuthentication(token);
//
//            if (authentication != null) {
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                        new UsernamePasswordAuthenticationToken(
//                                authentication.getPrincipal(),
//                                null,
//                                authentication.getAuthorities()
//                        );
//                usernamePasswordAuthenticationToken
//                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//            }
//        }
//        chain.doFilter(request, response);
//    }
//}
