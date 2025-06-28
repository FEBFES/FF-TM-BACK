package com.fftmback.authentication.repository;

import com.febfes.fftmback.domain.RoleName;
import com.fftmback.authentication.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(RoleName name);

}
