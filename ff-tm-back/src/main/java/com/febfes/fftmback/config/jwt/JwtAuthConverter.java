package com.febfes.fftmback.config.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtAuthConverter {

    private final JwtService jwtService;

    /**
     * Превращает raw JWT в Spring Security Authentication.
     * Если нужно, достаёт роль из клейма (например, "role") либо из внешнего заголовка (см. перегрузку ниже).
     */
    public Authentication convert(String token) {
        return convert(token, null);
    }

    /**
     * Используй эту перегрузку, если роль прилетает отдельной метаданной (например, "x-user-role")
     * и в токене её нет или она не совпадает.
     */
    public Authentication convert(String token, String roleFromHeader) {
        Long userId = extract(token, claims -> claims.get("userId", Long.class));
        String username = extract(token, Claims::getSubject);

        String role = firstNonBlank(
                roleFromHeader,
                extract(token, c -> c.get("role", String.class))
        );

        Collection<SimpleGrantedAuthority> authorities =
                role == null ? List.of() : List.of(new SimpleGrantedAuthority(role));

        User principal = new User(userId, role, username);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private <T> T extract(String token, Function<Claims, T> extractor) {
        return jwtService.extractClaim(token, extractor);
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return null;
    }
}
