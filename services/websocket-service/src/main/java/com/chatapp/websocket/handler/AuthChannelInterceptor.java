package com.chatapp.websocket.handler;

import com.chatapp.security.service.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;

    public AuthChannelInterceptor(final JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && accessor.getCommand() != null) {
            switch (accessor.getCommand()) {
                case CONNECT:
                    final List<String> authHeaders = accessor.getNativeHeader("Authorization");
                    final String token = authHeaders.getFirst().replace("Bearer ", "");

                    if (this.jwtService.isTokenValid(token)) {
                        final String username = this.jwtService.extractUsername(token);
                        final UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, null);

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        accessor.setUser(authentication);
                    } else {
                        throw new SecurityException("Invalid token");
                    }
                    break;
                case SEND, SUBSCRIBE:
                    if (accessor.getUser() == null) {
                        throw new SecurityException("Authentication required");
                    }
                    break;
                default:
                    break;
            }
        }
        return message;
    }

}
