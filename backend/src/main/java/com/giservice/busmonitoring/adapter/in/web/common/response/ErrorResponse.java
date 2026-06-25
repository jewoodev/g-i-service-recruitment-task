package com.giservice.busmonitoring.adapter.in.web.common.response;

import java.time.LocalDateTime;

public record ErrorResponse(
		String message,
		LocalDateTime timestamp
) {

	public static ErrorResponse of(String message) {
		return new ErrorResponse(message, LocalDateTime.now());
	}
}
