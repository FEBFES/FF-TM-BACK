package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByFileUrn(String fileUrn);

    List<FileEntity> findAllByEntityIdAndEntityType(Long entityId, EntityType entityType);

    boolean existsByEntityIdAndEntityType(Long entityId, EntityType entityType);
}
