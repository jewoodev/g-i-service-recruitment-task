package com.giservice.busmonitoring.adapter.in.web.event;

import com.giservice.busmonitoring.adapter.in.web.event.response.EventResponse;
import com.giservice.busmonitoring.application.provided.event.EventQueryProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

	private final EventQueryProvider eventQueryProvider;

	public EventController(EventQueryProvider eventQueryProvider) {
		this.eventQueryProvider = eventQueryProvider;
	}

	@GetMapping("/recent")
	public List<EventResponse> getRecentEvents(@RequestParam(defaultValue = "20") int limit) {
		return eventQueryProvider.getRecentEvents(limit).stream()
			.map(EventResponse::from)
			.toList();
	}
}
