package com.giservice.busmonitoring.adapter.in.web.bus.response;

import com.giservice.busmonitoring.domain.bus.BusCameraStatus;
import com.giservice.busmonitoring.domain.bus.CameraType;

import java.time.LocalDateTime;

public record CameraStatusResponse(
		CameraType cameraType,
		boolean receiving,
		String streamUrl,
		LocalDateTime lastReceivedAt
) {

	public static CameraStatusResponse from(BusCameraStatus cameraStatus) {
		return new CameraStatusResponse(
			cameraStatus.cameraType(),
			cameraStatus.receiving(),
			cameraStatus.streamUrl(),
			cameraStatus.lastReceivedAt()
		);
	}
}
