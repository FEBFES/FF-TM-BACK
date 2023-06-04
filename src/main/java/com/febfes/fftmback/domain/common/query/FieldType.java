package com.febfes.fftmback.domain.common.query;

import com.febfes.fftmback.domain.dao.BaseView;
import com.febfes.fftmback.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.febfes.fftmback.util.DateUtils.STANDARD_DATE_PATTERN;

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

    STRING {
        public Object parse(String value) {
            return value;
        }
    },

    BASEVIEW {
        public Object parse(String value) {
            return JsonUtils.convertStringToObject(value, BaseView.class);
        }
    };

    public abstract Object parse(String value);
}
