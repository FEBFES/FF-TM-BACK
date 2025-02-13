package com.febfes.fftmback.domain.common;

import com.febfes.fftmback.util.FileUtils;
import org.springframework.web.multipart.MultipartFile;

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
            return String.format(FileUtils.USER_PIC_URN, Long.parseLong(idForUrn));
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
        return "%s%s.%s".formatted(folderPath, idForPath, FileUtils.getExtension(file.getOriginalFilename()));
    }
}
