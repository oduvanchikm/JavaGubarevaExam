package org.example.tokenauth.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.tokenauth.service.TokenVerification;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_PREFIX = "Bearer ";

    private final TokenVerification tokenVerification;

    public TokenAuthenticationFilter(TokenVerification tokenVerification) {
        this.tokenVerification = tokenVerification;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(AUTH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenStr = authHeader.substring(AUTH_PREFIX.length()).trim();

        try {
            UUID token = UUID.fromString(tokenStr);

            if (tokenVerification.verify(token)) {
                var authentication = new PreAuthenticatedAuthenticationToken(
                        token,
                        null
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
