package com.febfes.fftmback.domain.common;

import com.febfes.fftmback.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
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
        public String getFilePath(MultipartFile file, String idForPath) {
            return "%s%s.%s".formatted(filesFolder,
                    idForPath, FileUtils.getExtension(file.getOriginalFilename()));
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
        public String getFilePath(MultipartFile file, String idForPath) {
            return "%s%s.%s".formatted(userPicFolder,
                    idForPath, FileUtils.getExtension(file.getOriginalFilename()));
        }
    };

    @Value("${files.folder}")
    private static String filesFolder;

    @Value("${user-pic.folder}")
    private static String userPicFolder;

    public abstract String getIdForPath(Long userId, String uuid);

    public abstract String getFileUrn(String idForUrn);

    public abstract String getFilePath(MultipartFile file, String idForPath);
}
