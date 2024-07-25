package org.example.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.utils.JwtTokenUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Log4j2
@AllArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     Filters requests and validates JWT tokens for authorization.
     @param request     the HTTP request
     @param response    the HTTP response
     @param filterChain the filter chain
     @throws ServletException if an error occurs during the filtering process
     @throws IOException      if an I/O error occurs
     **/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath();

        // Bypass authentication for health check and auth endpoints
        if (servletPath.startsWith("/api/health") ||
                servletPath.startsWith("/api/auth/register") ||
                servletPath.startsWith("/api/auth/login")) {
            log.debug("Skipping authentication for {}", servletPath);
            filterChain.doFilter(request, response);
            return;
        }

        String tokenType = "Bearer ";
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        // Check for the presence and validity of the authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith(tokenType)) {
            String accessToken = authorizationHeader.substring(tokenType.length());
            try {
                // Decode JWT token
                DecodedJWT decodedJWT = jwtTokenUtil.decodeJWT(accessToken);
                String userName = decodedJWT.getSubject();

                // Load user details and set the authentication context
                User user = (User) userDetailsService.loadUserByUsername(userName);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("User {} authenticated successfully", userName);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                log.error("Failed to authenticate user", e);
                throw new AccessDeniedException("Invalid access token.");
            }
        } else {
            log.warn("Missing or invalid authorization header");
            throw new AccessDeniedException("Missing access token.");
        }
    }
}