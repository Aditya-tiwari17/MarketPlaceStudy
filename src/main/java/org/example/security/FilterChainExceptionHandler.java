package org.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FilterChainExceptionHandler extends OncePerRequestFilter {
    /**
     Processes the request and handles exceptions that occur during filtering.
     @param request       the HTTP request
     @param response      the HTTP response
     @param filterChain   the filter chain
     @throws IOException  if an input or output exception occurs
     **/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            // Proceed with the next filter in the chain
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            // Log the exception
            log.error("Exception occurred during request processing: {}", ex.getMessage());

            // Set the response status to UNAUTHORIZED
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // Set error message header
            response.setHeader("error", ex.getMessage());

            // Prepare error response details
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("timestamp", new Date().toString());
            errorDetails.put("error_message", ex.getMessage());

            // Set response content type and write the error details
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), errorDetails);
        }
    }
}
