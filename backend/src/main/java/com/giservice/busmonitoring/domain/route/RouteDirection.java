package com.giservice.busmonitoring.domain.route;

public record RouteDirection(
		Long id,
		Route route,
		RouteDirectionType directionType,
		String originName,
		String destinationName
) {

	public String displayName() {
		return originName + " -> " + destinationName;
	}
}
