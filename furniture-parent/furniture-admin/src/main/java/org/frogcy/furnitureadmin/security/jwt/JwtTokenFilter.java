package org.frogcy.furnitureadmin.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.frogcy.furnitureadmin.security.CustomUserDetails;
import org.frogcy.furniturecommon.entity.Role;
import org.frogcy.furniturecommon.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    JwtUtility jwtUtil;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!hasAuthorizationBearer(request)){
            filterChain.doFilter(request, response);
            return;
        }

        String token = getBearerToken(request);
        LOGGER.info("Token: "+ token);
        try {
            Claims claims = jwtUtil.validateToken(token);

            LOGGER.info("ROLE TOKEN" + claims.get("roles"));
            UserDetails userDetails = getUserDetails(claims);

            setAuthenticationContext(userDetails, request);

            filterChain.doFilter(request, response);

            clearAuthenticationContext();
        } catch (JwtValidationException e) {
            LOGGER.error(e.getMessage(), e);
            exceptionResolver.resolveException(request, response, null, e);
        }

    }

    private void clearAuthenticationContext() {
        SecurityContextHolder.clearContext();
    }
    private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        LOGGER.info("User Authorites: " + authentication.getAuthorities());

        LOGGER.info("Role" + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }
    private UserDetails getUserDetails(Claims claims) {
        String subject = (String) claims.get(Claims.SUBJECT);
        String[] array = subject.split(",");

        Integer id =  Integer.valueOf(array[0].trim());
        String email = array[1];

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        String roles = (String) claims.get("roles");
        roles = roles.replace("[", "").replace("]", "");
        String[] rolesName = roles.split(",");
        for (String aRoleName : rolesName){
            user.addRole(new Role(aRoleName));
        }

        return new CustomUserDetails(user);
    }

    private String getBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String[] array = header.split(" ");
        if(array.length == 2){
            return array[1];
        }
        return null;
    }
    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        LOGGER.info("Authorization Header: "+ header);
        return !ObjectUtils.isEmpty(header) && header.startsWith("Bearer");
    }
}
