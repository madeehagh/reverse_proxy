package com.reverse.proxy.demo.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reverse.proxy.demo.constants.ProxyConstants;
import com.reverse.proxy.demo.exception.ApplicationException;
import com.reverse.proxy.demo.exception.RateLimitExceededException;
import com.reverse.proxy.demo.entity.User;
import com.reverse.proxy.demo.response.ErrorResponse;
import com.reverse.proxy.demo.service.UserService;
import com.reverse.proxy.demo.service.UserTrackingService;
import com.reverse.proxy.demo.util.JwtTokenUtil;
import com.reverse.proxy.demo.util.ProxyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private UserTrackingService userTrackingService;
    
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String userName = null;
        String jwt = null;
        User userDetails = null;
        
        try {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            userName = jwtTokenUtil.extractUserName(jwt);
        }
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userDetails = this.userService.findByUserName(userName);
            validateToken(httpServletRequest, jwt, userDetails);
            if (userTrackingService.isRequestLimitPermitted(userDetails, httpServletRequest.getRequestURI()))
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            else
                throw new RateLimitExceededException("too many requests for user " + userName + ". Retry after sometime!");
        }
        if (ProxyUtil.byPassAuthRequest(httpServletRequest.getRequestURI()))
            filterChain.doFilter(httpServletRequest, httpServletResponse);

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpServletResponse.getWriter().write(convertObjectToJson(errorResponse));
        }
    }

    private void validateToken(HttpServletRequest httpServletRequest, String jwt, User userDetails) {
        if (jwtTokenUtil.validateToken(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else{
            throw new ApplicationException("Invalid Token");
        }
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null)  return null;
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}

