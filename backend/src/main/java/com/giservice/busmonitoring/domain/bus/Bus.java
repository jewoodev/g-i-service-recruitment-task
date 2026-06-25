package com.giservice.busmonitoring.domain.bus;

import com.giservice.busmonitoring.domain.route.RouteDirection;
import com.giservice.busmonitoring.domain.stop.Stop;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record Bus(
		Long id,
		String busNumber,
		String plateNumber,
		RouteDirection routeDirection,
		Stop currentStop,
		Stop nextStop,
		LocalDateTime lastCommunicationAt,
		BusPosition latestPosition,
		List<BusCameraStatus> cameraStatuses
) {

	private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

	public Bus {
		cameraStatuses = List.copyOf(cameraStatuses);
	}

	public BusCommunicationStatus communicationStatusAt(LocalDateTime referenceTime) {
		Duration elapsed = Duration.between(lastCommunicationAt, referenceTime);
		if (!elapsed.minus(ONLINE_THRESHOLD).isPositive()) {
			return BusCommunicationStatus.ONLINE;
		}
		return BusCommunicationStatus.OFFLINE;
	}

	public BigDecimal currentSpeed() {
		if (latestPosition == null) {
			return BigDecimal.ZERO;
		}
		return latestPosition.speed();
	}
}
