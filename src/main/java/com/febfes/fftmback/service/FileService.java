package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    FileEntity getFile(String fileUrn);

    List<FileEntity> getFilesByEntityId(Long entityId, EntityType entityType);

    byte[] getFileContent(String idForUrn, EntityType entityType) throws IOException;

    FileEntity saveFile(Long userId, Long entityId, EntityType entityType, MultipartFile file);

    void deleteFileById(Long fileId);
}
