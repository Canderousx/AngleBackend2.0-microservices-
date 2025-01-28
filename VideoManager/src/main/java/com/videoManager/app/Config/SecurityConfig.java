package com.videoManager.app.Config;

import com.videoManager.app.Config.Filters.JwtAuthFilter;
import com.videoManager.app.Config.Filters.RequestsLogger;
import com.videoManager.app.Config.Services.JwtService;
import com.videoManager.app.Models.EnvironmentVariables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.CacheControl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private final EnvironmentVariables environmentVariables;

    public SecurityConfig(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Bean
    public String[]permitAllEndpoints(){
        return new String[]{
                "/videos/**",
                "/search/**",
                "/manage/registerView",
                "/media/thumbnails/**"
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/thumbnails/**")
                .addResourceLocations("file:/"+environmentVariables.getThumbnailsPath())
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic());
        registry.addResourceHandler("/videos/watch/**")
                .addResourceLocations("file:/"+environmentVariables.getHlsPath());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,String[]permitAllEndpoints) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/videos/getBySubscribed").authenticated()
                        .requestMatchers(permitAllEndpoints).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestsLogger(), JwtAuthFilter.class)
                .build();
    }

    @Bean
    JwtAuthFilter jwtAuthFilter(){
        return new JwtAuthFilter(jwtService(),permitAllEndpoints());
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
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }



}
