package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.example.entities.Actor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenUtil {
    @Value("${auth.jwt-secret-key}")
    private String secretKey;

    @Value("${auth.jwt-expiration-ms}")
    private long accessTokenExpiration;

    /**
     Gets the signing algorithm for JWT based on the secret key.
     @return the HMAC256 signing algorithm
     **/
    private Algorithm getSigningAlgorithm() {
        return Algorithm.HMAC256(secretKey.getBytes());
    }

    /**
     Decodes a JWT token and returns the decoded JWT object.
     @param token the JWT token to decode
     @return the decoded JWT
     @throws JwtException if the token is invalid or expired
     **/
    public DecodedJWT decodeJWT(String token) {
        try {
            log.info("Decoding JWT token...");
            Algorithm algorithm = getSigningAlgorithm();
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (TokenExpiredException ex) {
            log.error("JWT token has expired.", ex);
            throw new JwtException("JWT token has expired.");
        } catch (JWTDecodeException ex) {
            log.error("Invalid JWT token.", ex);
            throw new JwtException("Invalid JWT token.");
        } catch (SignatureVerificationException ex) {
            log.error("Invalid JWT signature.", ex);
            throw new JwtException("Invalid JWT signature.");
        } catch (AlgorithmMismatchException ex) {
            log.error("Signing algorithm mismatch.", ex);
            throw new JwtException("Signing algorithm mismatch.");
        }
    }

    /**
     Generates an access token for the given user and roles.
     @param user the user for whom the token is generated
     @param roles the roles of the user
     @return the generated JWT token
     **/
    public String generateAccessToken(Actor user, List<String> roles) {
        log.info("Generating access token for user: {}", user.getUsername());
        Algorithm algorithm = getSigningAlgorithm();
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .withClaim("token_type", "access")
                .withClaim("user_id", user.getId())
                .withClaim("roles", roles)
                .sign(algorithm);
    }
}
