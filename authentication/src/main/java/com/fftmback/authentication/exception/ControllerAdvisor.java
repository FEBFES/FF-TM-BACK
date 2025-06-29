package com.fftmback.authentication.exception;

import com.febfes.fftmback.dto.ErrorDto;
import com.febfes.fftmback.dto.ErrorType;
import com.febfes.fftmback.dto.StatusError;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerAdvisor {

    private static final String LOG_MSG = "Handled %s.";

    @ExceptionHandler({ExpiredJwtException.class, TokenExpiredException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Hidden
    public ErrorDto handleExpiredJwtException(
            RuntimeException ex
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createExceptionResponseBody(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @Hidden
    public ErrorDto handleGlobalException(
            Exception ex
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createExceptionResponseBody(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private ErrorDto createExceptionResponseBody(
            HttpStatus status,
            Exception ex
    ) {
        return new ErrorDto(
                status.value(),
                StatusError.UNDEFINED,
                ErrorType.UNDEFINED,
                LocalDateTime.now(),
                ex.getMessage(),
                null
        );
    }
}
