package com.febfes.fftmback.domain.common.query;

import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.febfes.fftmback.util.DateProvider.STANDARD_DATE_PATTERN;

@Slf4j
public enum FieldType {

    BOOLEAN {
        public Object parse(String value) {
            return Boolean.valueOf(value);
        }
    },

    DATE {
        public Object parse(String value) {
            Object date = null;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(STANDARD_DATE_PATTERN);
                date = LocalDateTime.parse(value, formatter);
            } catch (Exception e) {
                log.error("Failed parse field type DATE {}", e.getMessage());
            }

            return date;
        }
    },

    NUMBER {
        public Object parse(String value) {
            Object number = null;
            try {
                number = NumberFormat.getInstance().parse(value);
            } catch (ParseException e) {
                log.error("Failed parse field type NUMBER {}", e.getMessage());
            }

            return number;
        }
    },

//    TODO: after testing delete it if it's necessary
//    DOUBLE {
//        public Object parse(String value) {
//            return Double.valueOf(value);
//        }
//    },
//
//    INTEGER {
//        public Object parse(String value) {
//            return Integer.valueOf(value);
//        }
//    },
//
//    LONG {
//        public Object parse(String value) {
//            return Long.valueOf(value);
//        }
//    },
//
//    CHAR {
//        public Object parse(String value) {
//            return value.charAt(0);
//        }
//    },

    STRING {
        public Object parse(String value) {
            return value;
        }
    };

    public abstract Object parse(String value);
}
