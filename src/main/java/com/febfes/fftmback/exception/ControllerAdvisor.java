package com.febfes.fftmback.exception;

import com.febfes.fftmback.dto.ApiErrorDto;
import com.febfes.fftmback.util.DateUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerAdvisor {

    private static final String LOG_MESSAGE = "Message: {}.\nStack trace: {}";

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Hidden
    public ApiErrorDto handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MESSAGE, ex.getMessage(), Arrays.toString(ex.getStackTrace()));
        return createResponseBodyForExceptions(HttpStatus.NOT_FOUND, EntityNotFoundException.class.getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    public ApiErrorDto handleEntityAlreadyExistsException(
            EntityAlreadyExistsException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MESSAGE, ex.getMessage(), Arrays.toString(ex.getStackTrace()));
        return createResponseBodyForExceptions(HttpStatus.CONFLICT, EntityAlreadyExistsException.class.getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @Hidden
    public ApiErrorDto handleConstraintViolationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest httpRequest
    ) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        log.error(LOG_MESSAGE, errors, Arrays.toString(ex.getStackTrace()));
        return createResponseBodyForExceptions(HttpStatus.UNPROCESSABLE_ENTITY, MethodArgumentNotValidException.class.getSimpleName(),
                errors.toString(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Hidden
    public ApiErrorDto handleExpiredJwtException(
            ExpiredJwtException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MESSAGE, ex.getMessage(), Arrays.toString(ex.getStackTrace()));
        return createResponseBodyForExceptions(HttpStatus.UNAUTHORIZED, ExpiredJwtException.class.getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Hidden
    public ApiErrorDto handleTokenRefreshException(
            RefreshTokenExpiredException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MESSAGE, ex.getMessage(), Arrays.toString(ex.getStackTrace()));
        return createResponseBodyForExceptions(HttpStatus.UNAUTHORIZED, RefreshTokenExpiredException.class.getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler({Exception.class, SaveFileException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @Hidden
    public ApiErrorDto handleGlobalException(
            Exception ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MESSAGE, ex.getMessage(), Arrays.toString(ex.getStackTrace()));
        return createResponseBodyForExceptions(HttpStatus.INTERNAL_SERVER_ERROR, ex.getClass().getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    private ApiErrorDto createResponseBodyForExceptions(
            HttpStatus status,
            List<String> errors,
            String message,
            String path
    ) {
        return new ApiErrorDto(
                DateUtils.getCurrentDate(),
                status.value(),
                errors,
                message,
                path
        );
    }

    private ApiErrorDto createResponseBodyForExceptions(
            HttpStatus status,
            String error,
            String message,
            String path
    ) {
        return createResponseBodyForExceptions(status, List.of(error), message, path);
    }
}
