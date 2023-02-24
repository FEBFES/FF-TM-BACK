package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.UserPicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPicRepository extends JpaRepository<UserPicEntity, Long> {

    Optional<UserPicEntity> getUserPicEntitiesByUserId(Long userId);

}
