package com.job.scheduler.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Mono<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
		return Mono.just(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Mono<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		return Mono.just(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}

	@ExceptionHandler(WebExchangeBindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Mono<ValidationErrorResponse> handleValidationExceptions(WebExchangeBindException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return Mono.just(new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Mono<ErrorResponse> handleGenericException(Exception ex) {
		return Mono.just(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred"));
	}

	public record ErrorResponse(int status, String message) {
	}

	public record ValidationErrorResponse(int status, String message, Map<String, String> errors) {
	}
}
