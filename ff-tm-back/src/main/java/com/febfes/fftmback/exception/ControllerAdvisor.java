package com.febfes.fftmback.exception;

import com.febfes.fftmback.dto.ErrorDto;
import com.febfes.fftmback.dto.ErrorType;
import com.febfes.fftmback.dto.StatusError;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

import static com.febfes.fftmback.dto.AuthError.createBaseError;
import static com.febfes.fftmback.util.DateUtils.getCurrentLocalDateTime;
import static java.util.Objects.isNull;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(1) // order 2 in febfes-commons
public class ControllerAdvisor {

    private static final String LOG_MSG = "Handled %s.";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @Hidden
    public ErrorDto handleConstraintViolationException(
            MethodArgumentNotValidException ex
    ) {
        ErrorType errorType = ErrorType.AUTH;
        Map<String, ?> errorMap = ex.getBindingResult().getFieldErrors().stream().findFirst()
                .map(err -> createBaseError(err.getObjectName(), err.getField(),
                        isNull(err.getRejectedValue()) ? null : err.getRejectedValue().toString(), errorType))
                .orElse(Collections.emptyMap());
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()));
        return new ErrorDto(HttpStatus.UNPROCESSABLE_ENTITY.value(), StatusError.ARGUMENT_NOT_VALID,
                errorType, getCurrentLocalDateTime(), ex.getMessage(), errorMap);
    }

    @ExceptionHandler({ExpiredJwtException.class, TokenExpiredException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Hidden
    public ErrorDto handleExpiredJwtException(
            RuntimeException ex
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()));
        return createExceptionResponseBody(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler({RoleCheckException.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @Hidden
    public ErrorDto handleRoleCheckException(
            RuntimeException ex
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()));
        return createExceptionResponseBody(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @Hidden
    public ErrorDto handleBadCredentialsException(
            BadCredentialsException ex
    ) {
        ErrorType errorType = ErrorType.AUTH;
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()));
        return new ErrorDto(HttpStatus.UNPROCESSABLE_ENTITY.value(), StatusError.BAD_CREDENTIALS,
                errorType, getCurrentLocalDateTime(), ex.getMessage(),
                createBaseError("User", "password", null, errorType));
    }

    @ExceptionHandler(ProjectColumnException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    public ErrorDto handleProjectColumnException(
            ProjectColumnException ex
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()));
        return createExceptionResponseBody(HttpStatus.CONFLICT, ex);
    }

    private ErrorDto createExceptionResponseBody(
            HttpStatus status,
            Exception ex
    ) {
        return new ErrorDto(
                status.value(),
                StatusError.UNDEFINED,
                ErrorType.UNDEFINED,
                getCurrentLocalDateTime(),
                ex.getMessage(),
                null
        );
    }
}
