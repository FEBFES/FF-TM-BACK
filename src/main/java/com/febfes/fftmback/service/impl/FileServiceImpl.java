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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
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
        return repository.findAllByEntityIdAndEntityType(entityId, entityType.name());
    }

    @Override
    public byte[] getFileContent(String idForUrn, EntityType entityType) throws IOException {
        String fileUrn = "";
        if (EntityType.USER_PIC.equals(entityType)) {
            fileUrn = String.format(FileUtils.USER_PIC_URN, Long.parseLong(idForUrn));
        } else if (EntityType.TASK.equals(entityType)) {
            fileUrn = String.format(FileUtils.TASK_FILE_URN, idForUrn);
        }
        FileEntity fileEntity = getFile(fileUrn);
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
        // TODO: need some optimization (because we find in repository if it exists and only then we save)
        FileEntity.FileEntityBuilder<?, ?> fileBuilder = FileEntity.builder()
                .userId(userId)
                .entityId(entityId)
                .name(file.getOriginalFilename())
                .entityType(entityType.name())
                .contentType(file.getContentType());
        if (EntityType.USER_PIC.equals(entityType)) {
            fileBuilder.fileUrn(String.format(FileUtils.USER_PIC_URN, userId))
                    .filePath(
                            "%s%d.%s".formatted(userPicFolder, userId, FileUtils.getExtension(file.getOriginalFilename()))
                    );
        } else if (EntityType.TASK.equals(entityType)) {
            fileBuilder.fileUrn(String.format(FileUtils.TASK_FILE_URN, uuid))
                    .filePath(
                            "%s%s.%s".formatted(filesFolder, uuid, FileUtils.getExtension(file.getOriginalFilename()))
                    );
        }
        FileEntity fileEntity = fileBuilder.build();
        try {
            file.transferTo(new File(fileEntity.getFilePath()));
            // TODO: i'm talking about this (look upper)
            if (!EntityType.USER_PIC.equals(entityType) || !repository.existsByEntityIdAndEntityType(entityId, entityType.name())) {
                repository.save(fileEntity);
            }
        } catch (IOException e) {
            throw new SaveFileException(file.getName());
        }
        log.info("File saved by user with id={}", userId);
        return fileEntity;
    }

    @Override
    public void deleteFileById(Long fileId) {
        repository.deleteById(fileId);
        log.info("Deleted file with id={}", fileId);
    }
}
