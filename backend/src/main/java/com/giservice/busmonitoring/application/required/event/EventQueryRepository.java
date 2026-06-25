package com.giservice.busmonitoring.application.required.event;

import com.giservice.busmonitoring.domain.event.BusEvent;

import java.util.List;

public interface EventQueryRepository {

	List<BusEvent> findRecent(int limit);
}
