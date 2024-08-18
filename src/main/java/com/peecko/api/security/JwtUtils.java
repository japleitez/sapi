package com.peecko.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.api.jwtSecret}")
    private String jwtSecret;

    /** 10 hours expiration = 36000000 = 1000 * 60 * 60 * 10 */
    @Value("${app.api.jwtExpirationMs:36000000}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        String jti = UUID.randomUUID().toString();

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
            .setSubject((userPrincipal.getUsername()))
            .setIssuedAt(new Date()).setId(jti)
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    public String getUserNameFromJwtToken(String authToken) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody().getSubject();
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
            return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody().getId();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateAuthToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

}
