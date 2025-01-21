package com.fftmback.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Long getUserIdByUsername(String username);
}
