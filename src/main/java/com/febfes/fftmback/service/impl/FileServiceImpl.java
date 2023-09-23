package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.SaveFileException;
import com.febfes.fftmback.repository.FileRepository;
import com.febfes.fftmback.service.FileService;
import com.febfes.fftmback.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository repository;

    @Value("${files.folder}")
    private String filesFolder;

    @Value("${user-pic.folder}")
    private String userPicFolder;

    @Override
    public FileEntity getFile(String fileUrn) {
        return repository.findByFileUrn(fileUrn)
                .orElseThrow(() -> new EntityNotFoundException(FileEntity.ENTITY_NAME, "file urn", fileUrn));
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
    public FileEntity saveFile(
            Long userId,
            Long entityId,
            EntityType entityType,
            MultipartFile file
    ) {
        String uuid = UUID.randomUUID().toString();
        String idForPath = getIdForPath(entityType, userId, uuid);
        FileEntity fileEntity = FileEntity.builder()
                .userId(userId)
                .entityId(entityId)
                .name(file.getOriginalFilename())
                .entityType(entityType)
                .contentType(file.getContentType())
                .fileUrn(getFileUrn(entityType, idForPath))
                .filePath(getFilePath(entityType, file, idForPath))
                .build();
        fileProcess(fileEntity, file, entityId, entityType);
        log.info("File saved by user with id={}", userId);
        return fileEntity;
    }

    @Override
    @Transactional
    public void deleteFileById(Long id) {
        repository.deleteById(id);
        log.info("Deleted file with id={}", id);
    }

    private void fileProcess(
            FileEntity fileEntity,
            MultipartFile file,
            Long entityId,
            EntityType entityType
    ) {
        Optional<FileEntity> firstFile = repository.findFirstByEntityIdAndEntityType(entityId, entityType);
        try {
            file.transferTo(new File(fileEntity.getFilePath()));
        } catch (IOException e) {
            throw new SaveFileException(file.getName());
        }
        if (EntityType.USER_PIC.equals(entityType) && firstFile.isPresent()) {
            // We create only one FileEntity if entityType=USER_PIC
            fileEntity.setId(firstFile.get().getId());
        }
        repository.save(fileEntity);
    }

    private String getIdForPath(EntityType entityType, Long userId, String uuid) {
        Optional<String> idForPath = Optional.ofNullable(entityType)
                .map(type -> {
                    if (EntityType.USER_PIC.equals(type)) {
                        return userId.toString();
                    } else if (EntityType.TASK.equals(type)) {
                        return uuid;
                    }
                    return null;
                });
        if (idForPath.isEmpty()) {
            throw new IllegalArgumentException("ID for URN cannot be null or empty");
        }
        return idForPath.get();
    }

    private String getFileUrn(EntityType entityType, String idForUrn) {
        Optional<String> fileUrn = Optional.ofNullable(entityType)
                .map(type -> {
                    if (EntityType.USER_PIC.equals(type)) {
                        return String.format(FileUtils.USER_PIC_URN, Long.parseLong(idForUrn));
                    } else if (EntityType.TASK.equals(type)) {
                        return String.format(FileUtils.TASK_FILE_URN, idForUrn);
                    }
                    return null;
                });
        if (fileUrn.isEmpty()) {
            throw new IllegalArgumentException("File URN cannot be null or empty");
        }
        return fileUrn.get();
    }

    private String getFilePath(EntityType entityType, MultipartFile file, String idForPath) {
        Optional<String> filePath = Optional.ofNullable(entityType)
                .map(type -> {
                    if (EntityType.USER_PIC.equals(type)) {
                        return "%s%s.%s".formatted(userPicFolder,
                                idForPath, FileUtils.getExtension(file.getOriginalFilename()));
                    } else if (EntityType.TASK.equals(type)) {
                        return "%s%s.%s".formatted(filesFolder,
                                idForPath, FileUtils.getExtension(file.getOriginalFilename()));
                    }
                    return null;
                });
        if (filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        return filePath.get();
    }
}
