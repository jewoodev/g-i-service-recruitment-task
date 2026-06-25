package com.giservice.busmonitoring.domain.common;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

public record Coordinate(
		BigDecimal latitude,
		BigDecimal longitude
) {

	private static final BigDecimal MIN_LATITUDE = BigDecimal.valueOf(-90);

	private static final BigDecimal MAX_LATITUDE = BigDecimal.valueOf(90);

	private static final BigDecimal MIN_LONGITUDE = BigDecimal.valueOf(-180);

	private static final BigDecimal MAX_LONGITUDE = BigDecimal.valueOf(180);

	public Coordinate {
		requireNonNull(latitude, "latitude must not be null");
		requireNonNull(longitude, "longitude must not be null");

		if (latitude.compareTo(MIN_LATITUDE) < 0 || latitude.compareTo(MAX_LATITUDE) > 0) {
			throw new IllegalArgumentException("latitude must be between -90 and 90");
		}
		if (longitude.compareTo(MIN_LONGITUDE) < 0 || longitude.compareTo(MAX_LONGITUDE) > 0) {
			throw new IllegalArgumentException("longitude must be between -180 and 180");
		}
	}
}
