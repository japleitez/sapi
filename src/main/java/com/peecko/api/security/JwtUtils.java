package com.peecko.api.security;

import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {

    /** 10 hours expiration = 36000000 = 1000 * 60 * 60 * 10 */
    @Value("${app.api.jwtExpirationMs:36000000}")
    private int jwtExpirationMs;

    private static final String JWT_SECRET = "Calcium.Copper.Iodine.Iron.Magnesium.Phosphorus.Potassium.Selenium.Sodium.Zinc";

    private static SecretKey secretKey = null;

    private static SecretKey getSecretKey() {
        if (secretKey == null) {
            byte[] secretBytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
            secretKey = Keys.hmacShaKeyFor(secretBytes);
        }
        return secretKey;
    }

    public String generateJwtToken(Authentication authentication) {

        String jti = UUID.randomUUID().toString();

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();


        return Jwts.builder()
            .setSubject((userPrincipal.getUsername()))
            .setIssuer("peecko.com")
            .setIssuedAt(new Date())
            .setId(jti)
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(getSecretKey(), SignatureAlgorithm.HS512) // Sign with HMAC SHA-512 and secret key
            .compact();
    }

    public String getJtiFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            return getJtiFromAuthToken(authToken);
        }
        return null;
    }

    public String getJtiFromAuthToken(String authToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(authToken).getBody().getId();
        } catch (Exception e) {
            return null;
        }
    }

    public Claims validateAuthToken(String authToken) {
        return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(authToken).getBody();
    }

}
