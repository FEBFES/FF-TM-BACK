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
    public List<FileEntity> getFilesByEntityId(Long entityId) {
        return repository.findAllByEntityId(entityId);
    }

    @Override
    public byte[] getFileContent(String idForUrn, EntityType entityType) throws IOException {
        String fileUrn = "";
        if (entityType.equals(EntityType.USER_PIC)) {
            fileUrn = String.format(FileUtils.USER_PIC_URN, Long.parseLong(idForUrn));
        } else if (entityType.equals(EntityType.TASK)) {
            fileUrn = String.format(FileUtils.TASK_FILE_URN, idForUrn);
        }
        FileEntity fileEntity = getFile(fileUrn);
        String filePath = fileEntity.getFilePath();
        log.info("Received file entity: {}", fileEntity);
        return Files.readAllBytes(new File(filePath).toPath());
    }

    @Override
    public void saveFile(
            Long userId,
            Long entityId,
            EntityType entityType,
            MultipartFile file
    ) {
        String uuid = UUID.randomUUID().toString();
        FileEntity.FileEntityBuilder fileBuilder = FileEntity.builder()
                .userId(userId)
                .entityId(entityId)
                .name(file.getOriginalFilename())
                .entityType(entityType.name())
                .contentType(file.getContentType());
        if (entityType.equals(EntityType.USER_PIC)) {
            fileBuilder.fileUrn(String.format(FileUtils.USER_PIC_URN, userId))
                    .filePath(
                            "%s%d.%s".formatted(userPicFolder, userId, FileUtils.getExtension(file.getOriginalFilename()))
                    );
        } else if (entityType.equals(EntityType.TASK)) {
            fileBuilder.fileUrn(String.format(FileUtils.TASK_FILE_URN, uuid))
                    .filePath(
                            "%s%s.%s".formatted(filesFolder, uuid, FileUtils.getExtension(file.getOriginalFilename()))
                    );
        }
        try {
            FileEntity fileEntity = fileBuilder.build();
            file.transferTo(new File(fileEntity.getFilePath()));
            repository.save(fileEntity);
        } catch (IOException e) {
            throw new SaveFileException(file.getName());
        }
        log.info("File saved by user with id={}", userId);
    }

    @Override
    public void deleteFileById(Long fileId) {
        repository.deleteById(fileId);
        log.info("Deleted file with id={}", fileId);
    }
}
