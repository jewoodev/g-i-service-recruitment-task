package com.giservice.busmonitoring.application.provided.event;

import com.giservice.busmonitoring.domain.event.BusEvent;

import java.util.List;

public interface EventQueryProvider {

	List<BusEvent> getRecentEvents(int limit);
}
