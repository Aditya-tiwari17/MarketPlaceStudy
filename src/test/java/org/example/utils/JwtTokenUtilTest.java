package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.JwtException;
import org.example.MarketPlaceApplication;
import org.example.entities.Actor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MarketPlaceApplication.class)
@TestPropertySource(properties = {
        "auth.jwt-secret-key=TestSecretKey",
        "auth.jwt-expiration-ms=3600000"
})
class JwtTokenUtilTest {
    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;


//    @BeforeEach
//    public void setUp() {
//        jwtTokenUtil = new JwtTokenUtil();
//    }

    @Test
    void generateAccessToken() {
        Actor user = Actor.builder()
                .id(123L)
                .username("AdityaT")
                .build();
        List<String> roles = List.of("ROLE_USER");
        String token = jwtTokenUtil.generateAccessToken(user, roles);

        assertNotNull(token);

        DecodedJWT decodedJWT = jwtTokenUtil.decodeJWT(token);

        assertEquals("AdityaT", decodedJWT.getSubject());
        assertEquals(123L, decodedJWT.getClaim("user_id").asLong());
        assertEquals("access", decodedJWT.getClaim("token_type").asString());
        assertEquals(roles, decodedJWT.getClaim("roles").asList(String.class));
        assertTrue(decodedJWT.getExpiresAt().after(new Date()));
    }

//    @Test
//    void decodeJWTValidToken() {
//        String token = jwtTokenUtil.generateAccessToken(user, List.of("ROLE_USER"));
//        DecodedJWT decodedJWT = jwtTokenUtil.decodeJWT(token);
//
//        assertNotNull(decodedJWT);
//        assertEquals("testUser", decodedJWT.getSubject());
//    }

//    @Test
//    void decodeJWTExpiredToken() {
//        ReflectionTestUtils.setField(jwtTokenUtil, "accessTokenExpiration", -1000); // Set expiration in the past
//        String expiredToken = jwtTokenUtil.generateAccessToken(user, List.of("ROLE_USER"));
//
//        assertThrows(JwtException.class, () -> jwtTokenUtil.decodeJWT(expiredToken), "JWT token has expired.");
//    }
//
//    @Test
//    void decodeJWTInvalidSignature() {
//        String token = jwtTokenUtil.generateAccessToken(user, List.of("ROLE_USER"));
//
//        // Simulate token tampering by changing the token's signature
//        String tamperedToken = token.substring(0, token.lastIndexOf('.') + 1) + "tampered";
//
//        assertThrows(JwtException.class, () -> jwtTokenUtil.decodeJWT(tamperedToken), "Invalid JWT signature.");
//    }
}
