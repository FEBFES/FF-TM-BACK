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

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerAdvisor {
    
    private static final String LOG_MSG = "Handled %s.";

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Hidden
    public ApiErrorDto handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createResponseBodyForExceptions(HttpStatus.NOT_FOUND, ex.getClass().getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    public ApiErrorDto handleEntityAlreadyExistsException(
            EntityAlreadyExistsException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createResponseBodyForExceptions(HttpStatus.CONFLICT, ex.getClass().getSimpleName(),
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
                .toList();
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createResponseBodyForExceptions(HttpStatus.UNPROCESSABLE_ENTITY, ex.getClass().getSimpleName(),
                errors.toString(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Hidden
    public ApiErrorDto handleExpiredJwtException(
            ExpiredJwtException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createResponseBodyForExceptions(HttpStatus.UNAUTHORIZED, ex.getClass().getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Hidden
    public ApiErrorDto handleTokenRefreshException(
            TokenExpiredException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createResponseBodyForExceptions(HttpStatus.UNAUTHORIZED, ex.getClass().getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(ProjectOwnerException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    public ApiErrorDto handleProjectOwnerException(
            ProjectOwnerException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createResponseBodyForExceptions(HttpStatus.CONFLICT, ex.getClass().getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(ProjectColumnException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    public ApiErrorDto handleProjectColumnException(
            ProjectColumnException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createResponseBodyForExceptions(HttpStatus.CONFLICT, ex.getClass().getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(RoleCheckException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @Hidden
    public ApiErrorDto handleRoleCheckException(
            RoleCheckException ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
        return createResponseBodyForExceptions(HttpStatus.FORBIDDEN, ex.getClass().getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler({Exception.class, SaveFileException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @Hidden
    public ApiErrorDto handleGlobalException(
            Exception ex,
            HttpServletRequest httpRequest
    ) {
        log.error(LOG_MSG.formatted(ex.getClass().getSimpleName()), ex);
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
