package com.febfes.fftmback.exception;

import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.error.ErrorDto;
import com.febfes.fftmback.dto.error.ErrorType;
import com.febfes.fftmback.dto.error.StatusError;
import com.febfes.fftmback.util.DateUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.febfes.fftmback.dto.error.AuthError.createBaseError;
import static java.util.Objects.isNull;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerAdvisor {

    private static final String LOG_MSG = "Handled %s.";

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Hidden
    public ErrorDto handleEntityNotFoundException(
            EntityNotFoundException ex
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createExceptionResponseBody(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    public ErrorDto handleEntityAlreadyExistsException(
            EntityAlreadyExistsException ex
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createExceptionResponseBody(HttpStatus.CONFLICT, ex);
    }

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
                .orElse(new HashMap<>());
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return new ErrorDto(HttpStatus.UNPROCESSABLE_ENTITY.value(), StatusError.ARGUMENT_NOT_VALID,
                errorType, DateUtils.getCurrentDate(), ex.getMessage(), errorMap);
    }

    @ExceptionHandler({ExpiredJwtException.class, TokenExpiredException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Hidden
    public ErrorDto handleExpiredJwtException(
            RuntimeException ex
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createExceptionResponseBody(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(RoleCheckException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @Hidden
    public ErrorDto handleRoleCheckException(
            RoleCheckException ex
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createExceptionResponseBody(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @Hidden
    public ErrorDto handleBadCredentialsException(
            BadCredentialsException ex
    ) {
        ErrorType errorType = ErrorType.AUTH;
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return new ErrorDto(HttpStatus.UNPROCESSABLE_ENTITY.value(), StatusError.BAD_CREDENTIALS,
                errorType, DateUtils.getCurrentDate(), ex.getMessage(),
                createBaseError(UserEntity.ENTITY_NAME, "password", null, errorType));
    }

    @ExceptionHandler(ProjectColumnException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    public ErrorDto handleProjectColumnException(
            ProjectColumnException ex
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createExceptionResponseBody(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler({Exception.class, SaveFileException.class})
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
            CustomException ex
    ) {
        return new ErrorDto(
                status.value(),
                ex.getStatusError(),
                ex.getErrorType(),
                DateUtils.getCurrentDate(),
                ex.getMessage(),
                ex.getBaseError()
        );
    }

    private ErrorDto createExceptionResponseBody(
            HttpStatus status,
            Exception ex
    ) {
        return new ErrorDto(
                status.value(),
                StatusError.UNDEFINED,
                ErrorType.UNDEFINED,
                DateUtils.getCurrentDate(),
                ex.getMessage(),
                null
        );
    }
}
