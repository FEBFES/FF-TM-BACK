package com.febfes.fftmback.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CaseUtils {

    public static String camelToSnake(final String camelStr) {
        String ret = camelStr.replaceAll("([a-z])([A-Z]+)", "$1_$2");
        return ret.toLowerCase();
    }
}
