package com.giservice.busmonitoring.domain.bus;

import java.time.LocalDateTime;

public record BusCameraStatus(
		Long id,
		Long busId,
		CameraType cameraType,
		boolean receiving,
		String streamUrl,
		LocalDateTime lastReceivedAt
) {
}
