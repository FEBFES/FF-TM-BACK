package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query(value = "DELETE FROM refresh_token rt WHERE rt.user_id = :userId", nativeQuery = true)
    void deleteByUserId(Long userId);

    @Query("SELECT CASE " +
            "WHEN COUNT(rt) > 0 then " +
            "true else false " +
            "end " +
            "from RefreshTokenEntity rt where rt.userEntity.id = :userId")
    boolean existsByUserId(Long userId);
}
