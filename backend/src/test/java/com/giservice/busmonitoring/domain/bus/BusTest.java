package com.giservice.busmonitoring.domain.bus;

import com.giservice.busmonitoring.domain.common.Coordinate;
import com.giservice.busmonitoring.domain.route.Route;
import com.giservice.busmonitoring.domain.route.RouteDirection;
import com.giservice.busmonitoring.domain.route.RouteDirectionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BusTest {

	private static final LocalDateTime REFERENCE_TIME = LocalDateTime.of(2026, 6, 25, 12, 0);

	@Test
	void communicationStatusIsOnlineAtFiveMinuteBoundary() {
		Bus bus = busCommunicatedAt(REFERENCE_TIME.minusMinutes(5));

		assertThat(bus.communicationStatusAt(REFERENCE_TIME)).isEqualTo(BusCommunicationStatus.ONLINE);
	}

	@Test
	void communicationStatusIsOfflineAfterFiveMinutes() {
		Bus bus = busCommunicatedAt(REFERENCE_TIME.minusMinutes(5).minusNanos(1));

		assertThat(bus.communicationStatusAt(REFERENCE_TIME)).isEqualTo(BusCommunicationStatus.OFFLINE);
	}

	@Test
	void currentSpeedReturnsZeroWhenLatestPositionDoesNotExist() {
		Bus bus = bus(REFERENCE_TIME, null);

		assertThat(bus.currentSpeed()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	void currentSpeedReturnsLatestPositionSpeed() {
		BusPosition latestPosition = new BusPosition(
			1L,
			1L,
			new Coordinate(new BigDecimal("37.5559460"), new BigDecimal("126.9723170")),
			new BigDecimal("31.50"),
			REFERENCE_TIME.minusMinutes(1)
		);
		Bus bus = bus(REFERENCE_TIME, latestPosition);

		assertThat(bus.currentSpeed()).isEqualByComparingTo("31.50");
	}

	private static Bus busCommunicatedAt(LocalDateTime lastCommunicationAt) {
		return bus(lastCommunicationAt, null);
	}

	private static Bus bus(LocalDateTime lastCommunicationAt, BusPosition latestPosition) {
		Route route = new Route(1L, "7016", "은평공영차고지 ↔ 상명대");
		RouteDirection routeDirection = new RouteDirection(
			1L,
			route,
			RouteDirectionType.OUTBOUND,
			"은평공영차고지",
			"상명대"
		);

		return new Bus(
			1L,
			"7016-01",
			"서울70사1234",
			routeDirection,
			null,
			null,
			lastCommunicationAt,
			latestPosition,
			List.of()
		);
	}
}
