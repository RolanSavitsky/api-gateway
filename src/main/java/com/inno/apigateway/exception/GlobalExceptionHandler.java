package com.inno.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebInputException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleServiceError(WebClientResponseException exception) {
        return switch (exception.getStatusCode().value()) {
            case 400 -> build(HttpStatus.BAD_REQUEST, "Invalid registration data");
            case 409 -> build(HttpStatus.CONFLICT, "Login or email already exists");
            default -> build(HttpStatus.SERVICE_UNAVAILABLE, "Service is unavailable");
        };
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(WebClientRequestException exception) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, "Service is unavailable");
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidation(WebExchangeBindException exception) {
        StringBuilder message = new StringBuilder();

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            message.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }

        return build(HttpStatus.BAD_REQUEST, message.toString());
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequest(ServerWebInputException exception) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request or invalid data format");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(error);
    }
}
