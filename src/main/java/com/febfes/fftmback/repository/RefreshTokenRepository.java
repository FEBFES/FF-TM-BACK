package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query(value = "DELETE FROM refresh_token rt WHERE rt.user_id = ?1", nativeQuery = true)
    void deleteByUserId(Long userId);
}
