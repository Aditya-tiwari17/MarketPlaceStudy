package org.example.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.security.CustomAuthorizationFilter;
import org.example.security.FilterChainExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebSecurityConfigTest {

    @Mock
    private CustomAuthorizationFilter customAuthorizationFilter;

    @Mock
    private FilterChainExceptionHandler filterChainExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSecurityFilterChain() throws Exception {

        // Simulate filter invocation
        customAuthorizationFilter.doFilter(request, response, filterChain);
        filterChainExceptionHandler.doFilter(request, response, filterChain);

        verify(customAuthorizationFilter, times(1)).doFilter(request, response, filterChain);
        verify(filterChainExceptionHandler, times(1)).doFilter(request, response, filterChain);
    }
}