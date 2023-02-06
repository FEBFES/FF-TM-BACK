package com.febfes.fftmback.exception;

import com.febfes.fftmback.dto.ApiErrorDto;
import com.febfes.fftmback.util.DateProvider;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private final DateProvider dateProvider;

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Hidden
    public ApiErrorDto handleEntityNotFoundException(
            EntityNotFoundException ex,
            WebRequest request,
            HttpServletRequest httpRequest
    ) {
        return createResponseBodyForExceptions(HttpStatus.NOT_FOUND, EntityNotFoundException.class.getSimpleName(),
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
