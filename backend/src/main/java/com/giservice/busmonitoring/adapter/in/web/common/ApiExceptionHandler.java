package com.giservice.busmonitoring.adapter.in.web.common;

import com.giservice.busmonitoring.adapter.in.web.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of(exception.getMessage()));
	}
}
