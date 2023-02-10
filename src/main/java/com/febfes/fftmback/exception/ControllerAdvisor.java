package com.febfes.fftmback.exception;

import com.febfes.fftmback.dto.ApiErrorDto;
import com.febfes.fftmback.util.DateProvider;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvisor {

    private final DateProvider dateProvider;

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Hidden
    public ApiErrorDto handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest httpRequest
    ) {
        return createResponseBodyForExceptions(HttpStatus.NOT_FOUND, EntityNotFoundException.class.getSimpleName(),
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
        return createResponseBodyForExceptions(HttpStatus.UNPROCESSABLE_ENTITY, MethodArgumentNotValidException.class.getSimpleName(),
                errors.toString(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @Hidden
    public ApiErrorDto handleGlobalException(
            Exception ex,
            HttpServletRequest httpRequest
    ) {
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
                dateProvider.getCurrentDate(),
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
