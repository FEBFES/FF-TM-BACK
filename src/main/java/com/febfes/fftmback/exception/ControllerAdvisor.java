package com.febfes.fftmback.exception;

import com.febfes.fftmback.util.DateProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private final DateProvider dateProvider;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex,
            WebRequest request,
            HttpServletRequest httpRequest
    ) {
        ApiError error = createResponseBodyForExceptions(HttpStatus.NOT_FOUND, EntityNotFoundException.class.getSimpleName(),
                ex.getMessage(), httpRequest.getRequestURI());

        return new ResponseEntity<>(error, HttpStatusCode.valueOf(error.getStatus()));
    }

    private ApiError createResponseBodyForExceptions(HttpStatus status, List<String> errors, String message, String path) {
        return ApiError.builder()
                .timestamp(dateProvider.getCurrentDate())
                .status(status.value())
                .errors(errors)
                .message(message)
                .path(path)
                .build();
    }

    private ApiError createResponseBodyForExceptions(HttpStatus status, String error, String message, String path) {
        return createResponseBodyForExceptions(status, List.of(error), message, path);
    }
}
