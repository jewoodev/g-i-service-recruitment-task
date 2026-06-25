package com.giservice.busmonitoring.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class BusMonitoringApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getBusesReturnsBusSummaries() throws Exception {
		mockMvc.perform(get("/api/v1/buses"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(5))
			.andExpect(jsonPath("$[0].busNumber").value("143-01"))
			.andExpect(jsonPath("$[0].directionType").value("OUTBOUND"))
			.andExpect(jsonPath("$[2].directionType").value("INBOUND"))
			.andExpect(jsonPath("$[0].communicationStatus").value("ONLINE"))
			.andExpect(jsonPath("$[2].communicationStatus").value("OFFLINE"));
	}

	@Test
	void getBusReturnsBusDetail() throws Exception {
		mockMvc.perform(get("/api/v1/buses/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.busNumber").value("7016-01"))
			.andExpect(jsonPath("$.routeNumber").value("7016"))
			.andExpect(jsonPath("$.directionType").value("OUTBOUND"))
			.andExpect(jsonPath("$.directionName").value("은평공영차고지 -> 상명대"))
			.andExpect(jsonPath("$.originName").value("은평공영차고지"))
			.andExpect(jsonPath("$.destinationName").value("상명대"))
			.andExpect(jsonPath("$.currentStopName").value("서울역버스환승센터"))
			.andExpect(jsonPath("$.cameraStatuses.length()").value(4));
	}

	@Test
	void getBusReturnsNotFoundForUnknownId() throws Exception {
		mockMvc.perform(get("/api/v1/buses/999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Bus not found: id=999"));
	}

	@Test
	void getLatestBusPositionsReturnsMapData() throws Exception {
		mockMvc.perform(get("/api/v1/buses/positions/latest"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(5))
			.andExpect(jsonPath("$[0].directionName").exists())
			.andExpect(jsonPath("$[0].latitude").exists())
			.andExpect(jsonPath("$[0].longitude").exists());
	}

	@Test
	void getLatestBusPositionReturnsSingleBusMapData() throws Exception {
		mockMvc.perform(get("/api/v1/buses/1/positions/latest"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.busId").value(1))
			.andExpect(jsonPath("$.busNumber").value("7016-01"))
			.andExpect(jsonPath("$.routeNumber").value("7016"))
			.andExpect(jsonPath("$.directionType").value("OUTBOUND"))
			.andExpect(jsonPath("$.latitude").value(37.5559460))
			.andExpect(jsonPath("$.longitude").value(126.9723170))
			.andExpect(jsonPath("$.speed").value(31.50))
			.andExpect(jsonPath("$.communicationStatus").value("ONLINE"))
			.andExpect(jsonPath("$.recordedAt").exists());
	}

	@Test
	void getRecentEventsReturnsEvents() throws Exception {
		mockMvc.perform(get("/api/v1/events/recent").param("limit", "3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(3))
			.andExpect(jsonPath("$[0].busId").value(3))
			.andExpect(jsonPath("$[0].busNumber").value("143-01"))
			.andExpect(jsonPath("$[0].routeNumber").value("143"))
			.andExpect(jsonPath("$[0].eventType").value("SUDDEN_ACCELERATION"))
			.andExpect(jsonPath("$[0].severity").value("MEDIUM"))
			.andExpect(jsonPath("$[0].latitude").value(37.5703770))
			.andExpect(jsonPath("$[0].longitude").value(126.9918950))
			.andExpect(jsonPath("$[0].occurredAt").exists());
	}
}
