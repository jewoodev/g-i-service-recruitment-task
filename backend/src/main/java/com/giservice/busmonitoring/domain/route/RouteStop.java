package com.giservice.busmonitoring.domain.route;

import com.giservice.busmonitoring.domain.stop.Stop;

public record RouteStop(
		Long id,
		RouteDirection routeDirection,
		Stop stop,
		Integer stopOrder
) {
}
