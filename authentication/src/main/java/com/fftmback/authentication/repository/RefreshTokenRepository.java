package com.fftmback.authentication.repository;

import com.fftmback.authentication.domain.RefreshTokenEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "refresh-token-entity-graph")
    Optional<RefreshTokenEntity> findByToken(String token);

    @Query("SELECT rt from RefreshTokenEntity rt where rt.userEntity.id = :userId")
    Optional<RefreshTokenEntity> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.token = :token, rt.expiryDate = :expiryDate WHERE rt.id = :id")
    void updateTokenAndExpiryById(@Param("id") Long id,
                                  @Param("token") String token,
                                  @Param("expiryDate") LocalDateTime expiryDate);
}
