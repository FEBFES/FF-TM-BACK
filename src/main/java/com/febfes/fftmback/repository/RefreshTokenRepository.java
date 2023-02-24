package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Query("SELECT rt from RefreshTokenEntity rt where rt.userEntity.id = :userId")
    Optional<RefreshTokenEntity> findByUserId(Long userId);
}
