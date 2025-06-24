package com.febfes.fftmback.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class FileUtils {

    public static final String TASK_FILE_URN = "/files/task/%s";
    public static final String USER_PIC_URN = "/files/user-pic/%d";

    public static String getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .map(ext -> ext.replaceAll("[^a-zA-Z0-9]", ""))
                .filter(ext -> !ext.isBlank())
                .orElse("");
    }
}
