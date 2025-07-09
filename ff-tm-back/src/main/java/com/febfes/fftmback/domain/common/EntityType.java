package com.febfes.fftmback.domain.common;

import com.febfes.fftmback.util.FileUrnUtils;
import com.febfes.fftmback.util.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;

public enum EntityType {

    TASK {
        @Override
        public String getIdForPath(Long userId, String uuid) {
            return uuid;
        }

        @Override
        public String getFileUrn(String idForUrn) {
            return String.format(FileUtils.TASK_FILE_URN, idForUrn);
        }

        @Override
        public String getPathPropertyName() {
            return "folders.task-files";
        }
    },
    USER_PIC {
        @Override
        public String getIdForPath(Long userId, String uuid) {
            return userId.toString();
        }

        @Override
        public String getFileUrn(String idForUrn) {
            return FileUrnUtils.getUserPicUrn(Long.parseLong(idForUrn));
        }

        @Override
        public String getPathPropertyName() {
            return "folders.user-pic";
        }
    };

    public abstract String getIdForPath(Long userId, String uuid);

    public abstract String getFileUrn(String idForUrn);

    public abstract String getPathPropertyName();

    public String getFilePath(MultipartFile file, String folderPath, String idForPath) {
        String sanitizedExt = FileUtils.getExtension(file.getOriginalFilename());
        return Paths.get(folderPath, idForPath + '.' + sanitizedExt).toString();
    }
}
