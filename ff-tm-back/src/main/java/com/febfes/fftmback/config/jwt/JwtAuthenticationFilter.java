package com.febfes.fftmback.config.jwt;

import com.febfes.fftmback.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.febfes.fftmback.util.RoleUtils.getRoles;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER = "Bearer ";

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isNull(authHeader) || !authHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(BEARER.length());
        String role = request.getHeader("X-user-role");
        Long userId = jwtService.extractClaim(jwt, claims -> claims.get("userId", Long.class));
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                new User(userId, role),
                null,
                getRoles(role)
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
//        final String username = jwtService.extractUsername(jwt);
//        if (nonNull(username) && isNull(SecurityContextHolder.getContext().getAuthentication())) {
//            // user is not authenticated yet
//            UserDetails userDetails = userService.loadUserByUsername(username);
//            if (jwtService.isTokenValid(jwt, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities()
//                );
//                authToken.setDetails(
//                        new WebAuthenticationDetailsSource().buildDetails(request)
//                );
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }

        filterChain.doFilter(request, response);
    }
}
