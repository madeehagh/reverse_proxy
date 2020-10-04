package com.reverse.proxy.demo.filter;


import com.reverse.proxy.demo.service.UserTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MetricFilter extends OncePerRequestFilter {

    @Autowired
    UserTrackingService userTrackingService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String req = httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI();

        filterChain.doFilter(httpServletRequest, httpServletResponse);

        int status = ((HttpServletResponse) httpServletResponse).getStatus();
        userTrackingService.updateSystemMetric(req, status);
    }

}
