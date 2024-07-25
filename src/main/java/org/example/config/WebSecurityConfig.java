package org.example.config;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.exceptions.MarketPlaceException;
import org.example.security.CustomAuthorizationFilter;
import org.example.security.FilterChainExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
@Log4j2
public class WebSecurityConfig {

    private final CustomAuthorizationFilter customAuthorizationFilter;
    private final FilterChainExceptionHandler filterChainExceptionHandler;

    /**
     Configures the security filter chain.
     @param http the HttpSecurity object to configure
     @return the configured SecurityFilterChain
     **/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection as it's not needed for a stateless API
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless session management
                    )
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers("/api/jobs/**", "/api/bids/**").authenticated() // Require authentication for specific endpoints
                            .anyRequest().permitAll() // Allow all other requests without authentication
                    )
                    .addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // Add custom filter before default authentication filter
                    .addFilterBefore(filterChainExceptionHandler, CustomAuthorizationFilter.class); // Add exception handler filter

            log.info("Security filter chain configured successfully.");
            return http.build();
        } catch (Exception ex) {
            throw new MarketPlaceException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!", ex.getMessage());
        }
    }

    /**
     Configures CORS settings for the application.
     @return the configured CorsConfigurationSource
     **/
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8083")); // Set allowed origins
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Set allowed HTTP methods
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Set allowed headers
        configuration.setAllowCredentials(true); // Allow credentials in CORS requests

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS configuration to all endpoints
        log.info("CORS configuration source set.");
        return source;
    }
}