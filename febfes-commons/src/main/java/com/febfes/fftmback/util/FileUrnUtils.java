package com.febfes.fftmback.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUrnUtils {

    private static final String USER_PIC_URN = "/files/user-pic/%d";

    public static String getUserPicUrn(Long userId) {
        return String.format(USER_PIC_URN, userId);
    }
}
