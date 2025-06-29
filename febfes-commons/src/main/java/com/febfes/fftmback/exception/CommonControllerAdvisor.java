package com.febfes.fftmback.exception;

import com.febfes.fftmback.dto.ErrorDto;
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
public class CommonControllerAdvisor {

    private static final String LOG_MSG = "Handled %s.";

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Hidden
    public ErrorDto handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createExceptionResponseBody(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    public ErrorDto handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createExceptionResponseBody(HttpStatus.CONFLICT, ex);
    }

    private ErrorDto createExceptionResponseBody(HttpStatus status, CustomException ex) {
        return new ErrorDto(
                status.value(),
                ex.getStatusError(),
                ex.getErrorType(),
                LocalDateTime.now(),
                ex.getMessage(),
                ex.getBaseError()
        );
    }
}
