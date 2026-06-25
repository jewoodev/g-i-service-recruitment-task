package com.giservice.busmonitoring.application.service.event;

import com.giservice.busmonitoring.application.required.event.EventQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventQueryServiceTest {

	@Mock
	private EventQueryRepository eventQueryRepository;

	@Test
	void getRecentEventsUsesRequestedLimit() {
		EventQueryService service = service();

		assertThat(service.getRecentEvents(20)).isEmpty();

		verify(eventQueryRepository).findRecent(20);
	}

	@Test
	void getRecentEventsUsesOneAsMinimumLimit() {
		EventQueryService service = service();

		assertThat(service.getRecentEvents(0)).isEmpty();

		verify(eventQueryRepository).findRecent(1);
	}

	@Test
	void getRecentEventsUsesOneHundredAsMaximumLimit() {
		EventQueryService service = service();

		assertThat(service.getRecentEvents(101)).isEmpty();

		verify(eventQueryRepository).findRecent(100);
	}

	private EventQueryService service() {
		when(eventQueryRepository.findRecent(anyInt())).thenReturn(List.of());
		return new EventQueryService(eventQueryRepository);
	}
}
