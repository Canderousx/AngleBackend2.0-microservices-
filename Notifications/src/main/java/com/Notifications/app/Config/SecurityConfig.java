package com.Notifications.app.Config;

import com.Notifications.app.Config.Filters.JwtAuthFilter;
import com.Notifications.app.Config.Filters.RequestsLogger;
import com.Notifications.app.Config.Services.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/ws/**","/send-test").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestsLogger(), JwtAuthFilter.class)
                .build();
    }



    @Bean
    JwtAuthFilter jwtAuthFilter(){
        return new JwtAuthFilter(jwtService());
    }

    @Bean
    RequestsLogger requestsLogger(){
        return new RequestsLogger();
    }

    @Bean
    JwtService jwtService(){
        return new JwtService();
    }



}
