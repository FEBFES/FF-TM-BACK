package com.febfes.fftmback.config;

import com.febfes.fftmback.config.jwt.JwtService;
import com.febfes.fftmback.config.jwt.User;
import com.febfes.fftmback.domain.RoleName;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.febfes.fftmback.config.jwt.JwtAuthenticationFilter.BEARER;
import static com.febfes.fftmback.util.RoleUtils.getRoles;

@Component
public class TestJwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Value("${custom-headers.user-role}")
    private String userRoleHeader;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER)) {
            try {
                String role = request.getHeader(userRoleHeader);
                final String jwt = header.substring(BEARER.length());
                Long userId = jwtService.extractClaim(jwt, claims -> claims.get("userId", Long.class));
                String username = jwtService.extractClaim(jwt, Claims::getSubject);
                User user = new User(userId, role != null ? role : RoleName.OWNER.name(), username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        getRoles(user.role())
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (NumberFormatException ignored) {
            }
        }
        filterChain.doFilter(request, response);
    }
}
