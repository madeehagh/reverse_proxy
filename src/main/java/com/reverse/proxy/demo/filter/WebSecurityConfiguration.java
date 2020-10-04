package com.reverse.proxy.demo.filter;

import io.jsonwebtoken.Jwt;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @Autowired
    MetricFilter metricFilter;


    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable()
                .authorizeRequests().antMatchers("/api/v1/register",
                "/api/v1/application-data").permitAll().
                anyRequest().authenticated().and().
                exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(metricFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public FilterRegistrationBean < JwtRequestFilter > filterRegistrationBean() {
        FilterRegistrationBean < JwtRequestFilter > registrationBean = new FilterRegistrationBean();
        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter();

        registrationBean.setFilter(jwtRequestFilter);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(2); //set precedence
        return registrationBean;
    }
}
