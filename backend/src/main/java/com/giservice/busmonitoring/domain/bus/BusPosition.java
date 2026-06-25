package com.giservice.busmonitoring.domain.bus;

import com.giservice.busmonitoring.domain.common.Coordinate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

public record BusPosition(
		Long id,
		Long busId,
		Coordinate coordinate,
		BigDecimal speed,
		LocalDateTime recordedAt
) {

	public BusPosition {
		requireNonNull(coordinate, "coordinate must not be null");
		requireNonNull(speed, "speed must not be null");
		requireNonNull(recordedAt, "recordedAt must not be null");

		if (speed.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("speed must not be negative");
		}
	}

	public Coordinate position() {
		return coordinate;
	}
}
