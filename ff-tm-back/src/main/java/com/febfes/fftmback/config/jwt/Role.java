package com.febfes.fftmback.config.jwt;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

@AllArgsConstructor
public class Role implements GrantedAuthority {

    @Serial
    private static final long serialVersionUID = 7371915914382586699L;

    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }
}
