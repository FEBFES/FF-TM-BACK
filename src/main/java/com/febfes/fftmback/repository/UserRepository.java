package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByEmailOrUsername(String email, String username);

    @Query("SELECT u.id FROM UserEntity u WHERE u.username = ?1")
    Long getIdByUsername(String username);
}
