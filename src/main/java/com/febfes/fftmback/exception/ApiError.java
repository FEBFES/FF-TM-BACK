package com.febfes.fftmback.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
public class ApiError {
    private Date timestamp;
    private Integer status;
    private List<String> errors;
    private String message;
    private String path;
}
