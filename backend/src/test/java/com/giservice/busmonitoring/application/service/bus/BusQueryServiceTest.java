package com.giservice.busmonitoring.application.service.bus;

import com.giservice.busmonitoring.application.required.bus.BusQueryRepository;
import com.giservice.busmonitoring.domain.bus.Bus;
import com.giservice.busmonitoring.domain.route.Route;
import com.giservice.busmonitoring.domain.route.RouteDirection;
import com.giservice.busmonitoring.domain.route.RouteDirectionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusQueryServiceTest {

	@Mock
	private BusQueryRepository busQueryRepository;

	@Test
	void getBusesReturnsRepositoryResults() {
		Bus bus = bus(1L);
		BusQueryService service = new BusQueryService(busQueryRepository);
		when(busQueryRepository.findAll()).thenReturn(List.of(bus));

		List<Bus> buses = service.getBuses();

		assertThat(buses).containsExactly(bus);
		verify(busQueryRepository).findAll();
	}

	@Test
	void getBusReturnsFoundBus() {
		Bus bus = bus(1L);
		BusQueryService service = new BusQueryService(busQueryRepository);
		when(busQueryRepository.findById(1L)).thenReturn(Optional.of(bus));

		Bus found = service.getBus(1L);

		assertThat(found).isSameAs(bus);
		verify(busQueryRepository).findById(1L);
	}

	@Test
	void getBusThrowsWhenBusDoesNotExist() {
		BusQueryService service = new BusQueryService(busQueryRepository);
		when(busQueryRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getBus(999L))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("Bus not found: id=999");

		verify(busQueryRepository).findById(999L);
	}

	private static Bus bus(Long id) {
		Route route = new Route(1L, "7016", "은평공영차고지 ↔ 상명대");
		RouteDirection routeDirection = new RouteDirection(
			1L,
			route,
			RouteDirectionType.OUTBOUND,
			"은평공영차고지",
			"상명대"
		);

		return new Bus(
			id,
			"7016-01",
			"서울70사1234",
			routeDirection,
			null,
			null,
			LocalDateTime.of(2026, 6, 25, 12, 0),
			null,
			List.of()
		);
	}
}
