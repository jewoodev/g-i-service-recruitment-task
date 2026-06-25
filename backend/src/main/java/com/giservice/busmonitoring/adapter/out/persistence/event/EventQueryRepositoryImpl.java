package com.giservice.busmonitoring.adapter.out.persistence.event;

import com.giservice.busmonitoring.application.required.event.EventQueryRepository;
import com.giservice.busmonitoring.domain.event.BusEvent;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventQueryRepositoryImpl implements EventQueryRepository {

	private final BusEventJpaRepository busEventJpaRepository;

	public EventQueryRepositoryImpl(BusEventJpaRepository busEventJpaRepository) {
		this.busEventJpaRepository = busEventJpaRepository;
	}

	@Override
	public List<BusEvent> findRecent(int limit) {
		return busEventJpaRepository.findAllByOrderByOccurredAtDesc(PageRequest.of(0, limit)).stream()
			.map(BusEventEntity::toDomain)
			.toList();
	}
}
