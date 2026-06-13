package com.peecko.api.security;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.mapper.ApsUserMapper;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.repository.InvalidJwtRepo;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.Optional;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final ApsUserRepo apsUserRepo;

    private final InvalidJwtRepo invalidJwtRepo;

    private static final Logger LOG = LoggerFactory.getLogger(AuthTokenFilter.class);

   public AuthTokenFilter(JwtUtils jwtUtils, ApsUserRepo apsUserRepo, InvalidJwtRepo invalidJwtRepo) {
      this.jwtUtils = jwtUtils;
      this.apsUserRepo = apsUserRepo;
      this.invalidJwtRepo = invalidJwtRepo;
   }


   @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                Claims claims = jwtUtils.validateAuthToken(jwt);
                if (claims != null && hasNotSignedOut(claims.getId())) {
                    String username = claims.getSubject();
                    Optional<ApsUser> optionalApsUser = apsUserRepo.findByUsername(username);
                    if (optionalApsUser.isPresent()) {
                        ApsUser apsUser = optionalApsUser.get();
                        request.setAttribute(Login.CURRENT_USER, apsUser);
                        UserDetails userDetails = ApsUserMapper.toUserDetails(optionalApsUser.get());
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }

            }
        } catch (Exception e) {
            LOG.error("Cannot set user authentication", e);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    boolean hasNotSignedOut(String jti) {
        return invalidJwtRepo.findByJti(jti).isEmpty();
    }

}
