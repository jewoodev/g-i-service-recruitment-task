package com.giservice.busmonitoring.domain.common;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinateTest {

	@Test
	void acceptsBoundaryValues() {
		Coordinate min = new Coordinate(BigDecimal.valueOf(-90), BigDecimal.valueOf(-180));
		Coordinate max = new Coordinate(BigDecimal.valueOf(90), BigDecimal.valueOf(180));

		assertThat(min.latitude()).isEqualByComparingTo("-90");
		assertThat(min.longitude()).isEqualByComparingTo("-180");
		assertThat(max.latitude()).isEqualByComparingTo("90");
		assertThat(max.longitude()).isEqualByComparingTo("180");
	}

	@Test
	void rejectsLatitudeOutOfRange() {
		assertThatThrownBy(() -> new Coordinate(new BigDecimal("-90.0001"), BigDecimal.ZERO))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("latitude must be between -90 and 90");

		assertThatThrownBy(() -> new Coordinate(new BigDecimal("90.0001"), BigDecimal.ZERO))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("latitude must be between -90 and 90");
	}

	@Test
	void rejectsLongitudeOutOfRange() {
		assertThatThrownBy(() -> new Coordinate(BigDecimal.ZERO, new BigDecimal("-180.0001")))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("longitude must be between -180 and 180");

		assertThatThrownBy(() -> new Coordinate(BigDecimal.ZERO, new BigDecimal("180.0001")))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("longitude must be between -180 and 180");
	}

	@Test
	void rejectsNullValues() {
		assertThatThrownBy(() -> new Coordinate(null, BigDecimal.ZERO))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("latitude must not be null");

		assertThatThrownBy(() -> new Coordinate(BigDecimal.ZERO, null))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("longitude must not be null");
	}
}
