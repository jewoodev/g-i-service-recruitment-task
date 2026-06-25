package com.giservice.busmonitoring.application.provided.bus;

import com.giservice.busmonitoring.domain.bus.Bus;

import java.util.List;

public interface BusQueryProvider {

	List<Bus> getBuses();

	Bus getBus(Long id);
}
