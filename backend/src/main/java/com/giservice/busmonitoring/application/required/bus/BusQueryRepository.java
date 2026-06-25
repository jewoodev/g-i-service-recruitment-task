package com.giservice.busmonitoring.application.required.bus;

import com.giservice.busmonitoring.domain.bus.Bus;

import java.util.List;
import java.util.Optional;

public interface BusQueryRepository {

	List<Bus> findAll();

	Optional<Bus> findById(Long id);
}
