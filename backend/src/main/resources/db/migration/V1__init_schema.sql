CREATE TABLE routes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    route_number VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_routes_route_number UNIQUE (route_number)
);

CREATE TABLE route_directions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    route_id BIGINT NOT NULL,
    direction_type VARCHAR(20) NOT NULL,
    origin_name VARCHAR(100) NOT NULL,
    destination_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_route_directions_route FOREIGN KEY (route_id) REFERENCES routes (id),
    CONSTRAINT uk_route_directions_route_type UNIQUE (route_id, direction_type)
);

CREATE TABLE stops (
    id BIGINT NOT NULL AUTO_INCREMENT,
    stop_number VARCHAR(30) NOT NULL,
    name VARCHAR(100) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_stops_stop_number UNIQUE (stop_number)
);

CREATE TABLE route_stops (
    id BIGINT NOT NULL AUTO_INCREMENT,
    route_direction_id BIGINT NOT NULL,
    stop_id BIGINT NOT NULL,
    stop_order INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_route_stops_route_direction FOREIGN KEY (route_direction_id) REFERENCES route_directions (id),
    CONSTRAINT fk_route_stops_stop FOREIGN KEY (stop_id) REFERENCES stops (id),
    CONSTRAINT uk_route_stops_direction_order UNIQUE (route_direction_id, stop_order),
    CONSTRAINT uk_route_stops_direction_stop UNIQUE (route_direction_id, stop_id)
);

CREATE TABLE buses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    bus_number VARCHAR(30) NOT NULL,
    plate_number VARCHAR(30) NOT NULL,
    route_direction_id BIGINT NOT NULL,
    current_stop_id BIGINT NULL,
    next_stop_id BIGINT NULL,
    last_communication_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_buses_bus_number UNIQUE (bus_number),
    CONSTRAINT uk_buses_plate_number UNIQUE (plate_number),
    CONSTRAINT fk_buses_route_direction FOREIGN KEY (route_direction_id) REFERENCES route_directions (id),
    CONSTRAINT fk_buses_current_stop FOREIGN KEY (current_stop_id) REFERENCES stops (id),
    CONSTRAINT fk_buses_next_stop FOREIGN KEY (next_stop_id) REFERENCES stops (id)
);

CREATE TABLE bus_positions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    bus_id BIGINT NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    speed DECIMAL(5, 2) NOT NULL,
    recorded_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_bus_positions_bus FOREIGN KEY (bus_id) REFERENCES buses (id)
);

CREATE INDEX idx_bus_positions_bus_recorded_at ON bus_positions (bus_id, recorded_at);

CREATE TABLE bus_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    bus_id BIGINT NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    description VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    occurred_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_bus_events_bus FOREIGN KEY (bus_id) REFERENCES buses (id)
);

CREATE INDEX idx_bus_events_occurred_at ON bus_events (occurred_at);

CREATE TABLE bus_camera_statuses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    bus_id BIGINT NOT NULL,
    camera_type VARCHAR(30) NOT NULL,
    receiving BOOLEAN NOT NULL,
    stream_url VARCHAR(255) NULL,
    last_received_at TIMESTAMP(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_bus_camera_statuses_bus FOREIGN KEY (bus_id) REFERENCES buses (id),
    CONSTRAINT uk_bus_camera_statuses_bus_camera UNIQUE (bus_id, camera_type)
);
