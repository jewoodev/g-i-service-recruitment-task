package com.giservice.busmonitoring.domain.bus;

import com.giservice.busmonitoring.domain.common.Coordinate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BusPositionTest {

	private static final LocalDateTime RECORDED_AT = LocalDateTime.of(2026, 6, 25, 12, 0);

	@Test
	void returnsCoordinateAsPosition() {
		Coordinate coordinate = new Coordinate(new BigDecimal("37.5559460"), new BigDecimal("126.9723170"));
		BusPosition position = new BusPosition(1L, 1L, coordinate, new BigDecimal("31.50"), RECORDED_AT);

		assertThat(position.position()).isEqualTo(coordinate);
	}

	@Test
	void rejectsNegativeSpeed() {
		assertThatThrownBy(() -> new BusPosition(
			1L,
			1L,
			new Coordinate(new BigDecimal("37.5559460"), new BigDecimal("126.9723170")),
			new BigDecimal("-0.01"),
			RECORDED_AT
		))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("speed must not be negative");
	}
}
