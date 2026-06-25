package com.giservice.busmonitoring.domain.stop;

import com.giservice.busmonitoring.domain.common.Coordinate;

public record Stop(
		Long id,
		String stopNumber,
		String name,
		Coordinate coordinate
) {

	public Coordinate position() {
		return coordinate;
	}
}
