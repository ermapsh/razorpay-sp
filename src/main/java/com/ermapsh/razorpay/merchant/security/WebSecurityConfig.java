package com.ermapsh.razorpay.merchant.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    private static final String[] JWT_ROUTES = {
            "/api/v1/auth/**", "/api/v1/merchant/**", "/api/v1/admin/**", "/actuator/**"
    };

    private static final String[] API_KEY_ROUTES = {
            "/api/v1/orders/**", "/api/v1/payments/**", "/api/v1/order/**", "/api/v1/vault/**"
    };

    @Bean
    public SecurityFilterChain jwtChain(HttpSecurity http){
        return http.
                securityMatcher(JWT_ROUTES).

                csrf(csrf->csrf.disable()).
                sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
                authorizeHttpRequests(auth-> auth.
                        requestMatchers("/api/v1/auth/signup", "/api/v1/auth/login").permitAll()).
                formLogin(form-> form.disable()).
                build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AppUserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}
