package com.giservice.busmonitoring.application.service.event;

import com.giservice.busmonitoring.application.provided.event.EventQueryProvider;
import com.giservice.busmonitoring.application.required.event.EventQueryRepository;
import com.giservice.busmonitoring.domain.event.BusEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventQueryService implements EventQueryProvider {

	private static final int MAX_LIMIT = 100;

	private final EventQueryRepository eventQueryRepository;

	public EventQueryService(EventQueryRepository eventQueryRepository) {
		this.eventQueryRepository = eventQueryRepository;
	}

	@Override
	public List<BusEvent> getRecentEvents(int limit) {
		int sanitizedLimit = Math.max(1, Math.min(limit, MAX_LIMIT));
		return eventQueryRepository.findRecent(sanitizedLimit);
	}
}
