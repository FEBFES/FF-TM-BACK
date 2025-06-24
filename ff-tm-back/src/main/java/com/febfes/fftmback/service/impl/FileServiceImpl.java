package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.repository.FileRepository;
import com.febfes.fftmback.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final Environment env;
    private final FileRepository repository;

    @Override
    public FileEntity getFile(String fileUrn) {
        return repository.findByFileUrn(fileUrn)
                .orElseThrow(Exceptions.fileNotFound(fileUrn));
    }

    @Override
    public List<FileEntity> getFilesByEntityId(Long entityId, EntityType entityType) {
        return repository.findAllByEntityIdAndEntityType(entityId, entityType);
    }

    @Override
    public byte[] getFileContent(String idForUrn, EntityType entityType) throws IOException {
        FileEntity fileEntity = getFile(getFileUrn(entityType, idForUrn));
        String filePath = fileEntity.getFilePath();
        log.info("Received file entity: {}", fileEntity);
        return Files.readAllBytes(new File(filePath).toPath());
    }

    @Override
    @Transactional
    public FileEntity saveFile(
            Long userId,
            Long entityId,
            EntityType entityType,
            MultipartFile file
    ) throws IOException {
        FileEntity fileEntity = createFileEntity(userId, entityId, entityType, file);
        try {
            fileProcess(fileEntity, file, entityId, entityType);
        } catch (RuntimeException | IOException e) {
            Files.deleteIfExists(new File(fileEntity.getFilePath()).toPath());
            throw e;
        }
        log.info("File entity with id={} saved by user with id={}", fileEntity.getId(), userId);
        return fileEntity;
    }

    @Override
    public void deleteFileById(Long id) {
        Optional<FileEntity> fileEntityOpt = repository.findById(id);
        fileEntityOpt.ifPresent(fileEntity -> {
            repository.deleteById(id);
            log.info("Deleted file entity with id={}", id);
            try {
                Files.deleteIfExists(Path.of(fileEntity.getFilePath()));
            } catch (IOException e) {
                log.warn("Failed to delete file by path {}", fileEntity.getFilePath(), e);
            }
        });
    }

    private void fileProcess(
            FileEntity fileEntity,
            MultipartFile file,
            Long entityId,
            EntityType entityType
    ) throws IOException {
        Path filePath = Path.of(fileEntity.getFilePath());
        file.transferTo(filePath.toFile());
        if (EntityType.USER_PIC.equals(entityType)) {
            repository.findFirstByEntityIdAndEntityType(entityId, entityType)
                    .ifPresent(firstFile -> fileEntity.setId(firstFile.getId()));
        }
        repository.save(fileEntity);
    }

    private String getIdForPath(EntityType entityType, Long userId, String uuid) {
        return Optional.ofNullable(entityType)
                .map(type -> type.getIdForPath(userId, uuid))
                .orElseThrow(() -> new IllegalArgumentException("ID for URN cannot be null or empty"));
    }

    private String getFileUrn(EntityType entityType, String idForUrn) {
        return Optional.ofNullable(entityType)
                .map(type -> type.getFileUrn(idForUrn))
                .orElseThrow(() -> new IllegalArgumentException("File URN cannot be null or empty"));
    }

    private String getFilePath(EntityType entityType, MultipartFile file, String idForPath) {
        if (entityType == null) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        String folderPath = env.getProperty(entityType.getPathPropertyName());
        if (folderPath == null) {
            throw new IllegalArgumentException("Property %s is not set".formatted(entityType.getPathPropertyName()));
        }
        return entityType.getFilePath(file, folderPath, idForPath);
    }

    private FileEntity createFileEntity(
            Long userId,
            Long entityId,
            EntityType entityType,
            MultipartFile file
    ) {
        String uuid = UUID.randomUUID().toString();
        String idForPath = getIdForPath(entityType, userId, uuid);
        return FileEntity.builder()
                .userId(userId)
                .entityId(entityId)
                .name(file.getOriginalFilename())
                .entityType(entityType)
                .contentType(file.getContentType())
                .fileUrn(getFileUrn(entityType, idForPath))
                .filePath(getFilePath(entityType, file, idForPath))
                .build();
    }
}
