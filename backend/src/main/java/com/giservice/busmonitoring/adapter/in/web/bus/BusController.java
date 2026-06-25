package com.giservice.busmonitoring.adapter.in.web.bus;

import com.giservice.busmonitoring.adapter.in.web.bus.response.BusDetailResponse;
import com.giservice.busmonitoring.adapter.in.web.bus.response.BusPositionResponse;
import com.giservice.busmonitoring.adapter.in.web.bus.response.BusSummaryResponse;
import com.giservice.busmonitoring.application.provided.bus.BusQueryProvider;
import com.giservice.busmonitoring.domain.bus.Bus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/buses")
public class BusController {

	private final BusQueryProvider busQueryProvider;

	public BusController(BusQueryProvider busQueryProvider) {
		this.busQueryProvider = busQueryProvider;
	}

	@GetMapping
	public List<BusSummaryResponse> getBuses() {
		LocalDateTime referenceTime = LocalDateTime.now();
		return busQueryProvider.getBuses().stream()
			.map(bus -> BusSummaryResponse.from(bus, referenceTime))
			.toList();
	}

	@GetMapping("/{busId}")
	public BusDetailResponse getBus(@PathVariable Long busId) {
		return BusDetailResponse.from(busQueryProvider.getBus(busId), LocalDateTime.now());
	}

	@GetMapping("/positions/latest")
	public List<BusPositionResponse> getLatestBusPositions() {
		LocalDateTime referenceTime = LocalDateTime.now();
		return busQueryProvider.getBuses().stream()
			.map(bus -> BusPositionResponse.from(bus, referenceTime))
			.toList();
	}

	@GetMapping("/{busId}/positions/latest")
	public BusPositionResponse getLatestBusPosition(@PathVariable Long busId) {
		Bus bus = busQueryProvider.getBus(busId);
		return BusPositionResponse.from(bus, LocalDateTime.now());
	}
}
