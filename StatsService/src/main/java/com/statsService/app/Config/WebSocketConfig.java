package com.statsService.app.Config;
import com.statsService.app.Config.Services.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200","http://192.168.100.36:4200")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(
                            ServerHttpRequest request,
                            ServerHttpResponse response,
                            WebSocketHandler wsHandler,
                            Map<String, Object> attributes) {
                        if (request instanceof ServletServerHttpRequest) {
                            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                            String ipAddress = servletRequest.getServletRequest().getHeader("X-Forwarded-For");
                            attributes.put("ip_address", ipAddress);
                            log.info("IP address added to session attributes: {}", ipAddress);
                        }
                        return true;
                    }

                    @Override
                    public void afterHandshake(
                            ServerHttpRequest request,
                            ServerHttpResponse response,
                            WebSocketHandler wsHandler, Exception ex) {
                    }
                });

    }

    private void handleAnonymousConnection(String ip,StompHeaderAccessor accessor){
        log.info("Handling Anonymous Connection");
        UsernamePasswordAuthenticationToken anonymousToken = new UsernamePasswordAuthenticationToken(
                "ANONYMOUS_"+ip,
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(anonymousToken);
        accessor.setUser(anonymousToken);
    }

    private void handleAuthenticatedConnection(String token,String ip,StompHeaderAccessor accessor){
        log.info("Handling Authenticated Connection");
        Claims claims = jwtService.extractAllClaims(token);
        String userId = claims.getSubject();
        log.info("Extracted UserId: {}", userId);
        List<?> rolesFromToken = claims.get("roles", List.class);
        log.info("Roles from Token: {}", rolesFromToken);
        Collection<GrantedAuthority> roles = rolesFromToken.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString().toUpperCase()))
                .collect(Collectors.toList());
        if (jwtService.validateToken(token, userId, ip)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    roles);
            SecurityContextHolder.getContext().setAuthentication(authToken);
            accessor.setUser(authToken);
            log.info("User Principal set to: {}", accessor.getUser());
        } else {
            log.warn("Token validation failed for userId: {}", userId);
            SecurityContextHolder.clearContext();
            handleAnonymousConnection(ip,accessor);
        }


    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authentication");
                    String ipAddress = accessor.getSessionAttributes().get("ip_address").toString();
                    log.info("WS USER IP ADDRESS: {}", ipAddress);
                    if(authHeader == null || !authHeader.startsWith("Bearer ")){
                        handleAnonymousConnection(ipAddress,accessor);
                    }else{
                        String token = authHeader.substring(7);
                        handleAuthenticatedConnection(token,ipAddress,accessor);
                    }
                }
                return message;
            }

        });
    }

}
