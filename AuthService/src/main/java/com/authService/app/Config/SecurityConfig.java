package com.authService.app.Config;

import com.authService.app.Config.Filters.JwtAuthFilter;
import com.authService.app.Config.Filters.RequestsLogger;
import com.authService.app.Config.Services.JwtService;
import com.authService.app.Config.Services.MyUserDetailsService;
import com.authService.app.Models.EnvironmentVariables;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableAsync(proxyTargetClass = true)
public class SecurityConfig implements WebMvcConfigurer {


    @Bean
    public String[]permitAllEndpoints(){
        return new String[]{
                "/signIn/**",
                "/signUp/**",
                "/accounts/media/**",
                "/accounts/getUserById",
                "/accounts/getUsername",
                "/accounts/emailExists",
                "/accounts/usernameExists",
                "/accounts/countSubscribers"
        };
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,String[]permitAllEndpoints) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers(permitAllEndpoints).permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestsLogger(), JwtAuthFilter.class)
                .build();

    }

    @Bean
    JwtAuthFilter jwtAuthFilter(){
        return new JwtAuthFilter(jwtService(),userDetailsService(),permitAllEndpoints());
    }

    @Bean
    RequestsLogger requestsLogger(){
        return new RequestsLogger();
    }

    @Bean
    JwtService jwtService(){
        return new JwtService();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    MyUserDetailsService userDetailsService(){
        return new MyUserDetailsService();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService());
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
