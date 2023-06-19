package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.repository.RoleRepository;
import com.febfes.fftmback.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleEntity> getRoles() {
        List<RoleEntity> roles = roleRepository.findAll();
        log.info("Received {} roles", roles);
        return roles;
    }
}
