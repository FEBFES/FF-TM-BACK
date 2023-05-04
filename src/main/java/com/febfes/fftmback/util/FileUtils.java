package com.febfes.fftmback.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class FileUtils {

    public static final String TASK_FILE_URN = "/%d/columns/%d/tasks/%d/files/%s";
    public static final String USER_PIC_URN = "/users/%d/user-pic";

    public static String getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("");
    }
}
