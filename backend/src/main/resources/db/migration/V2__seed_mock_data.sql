INSERT INTO routes (id, route_number, name) VALUES
(1, '7016', '은평공영차고지 ↔ 상명대'),
(2, '143', '정릉 ↔ 개포동'),
(3, '472', '개포동 ↔ 신촌');

INSERT INTO route_directions (id, route_id, direction_type, origin_name, destination_name) VALUES
(1, 1, 'OUTBOUND', '은평공영차고지', '상명대'),
(2, 1, 'INBOUND', '상명대', '은평공영차고지'),
(3, 2, 'OUTBOUND', '정릉', '개포동'),
(4, 2, 'INBOUND', '개포동', '정릉'),
(5, 3, 'OUTBOUND', '개포동', '신촌'),
(6, 3, 'INBOUND', '신촌', '개포동');

INSERT INTO stops (id, stop_number, name, latitude, longitude) VALUES
(1, '01123', '서울역버스환승센터', 37.5559460, 126.9723170),
(2, '02145', '시청앞', 37.5662950, 126.9779450),
(3, '03118', '광화문', 37.5718470, 126.9769150),
(4, '04132', '종로3가', 37.5703770, 126.9918950),
(5, '05107', '강남역', 37.4979520, 127.0276190),
(6, '06111', '교대역', 37.4934150, 127.0140800),
(7, '07152', '신촌오거리', 37.5551230, 126.9368890),
(8, '08164', '홍대입구역', 37.5571920, 126.9248730);

INSERT INTO route_stops (route_direction_id, stop_id, stop_order) VALUES
(1, 1, 1),
(1, 2, 2),
(1, 3, 3),
(2, 3, 1),
(2, 2, 2),
(2, 1, 3),
(3, 4, 1),
(3, 5, 2),
(3, 6, 3),
(4, 6, 1),
(4, 5, 2),
(4, 4, 3),
(5, 5, 1),
(5, 7, 2),
(5, 8, 3),
(6, 8, 1),
(6, 7, 2),
(6, 5, 3);

INSERT INTO buses (id, bus_number, plate_number, route_direction_id, current_stop_id, next_stop_id, last_communication_at) VALUES
(1, '7016-01', '서울70사1234', 1, 1, 2, TIMESTAMPADD(MINUTE, -2, CURRENT_TIMESTAMP)),
(2, '7016-02', '서울70사1255', 2, 3, 2, TIMESTAMPADD(MINUTE, -8, CURRENT_TIMESTAMP)),
(3, '143-01', '서울74사2401', 3, 4, 5, TIMESTAMPADD(MINUTE, -1, CURRENT_TIMESTAMP)),
(4, '472-01', '서울75사3811', 5, 5, 7, TIMESTAMPADD(MINUTE, -4, CURRENT_TIMESTAMP)),
(5, '472-02', '서울75사3822', 6, 8, 7, TIMESTAMPADD(MINUTE, -12, CURRENT_TIMESTAMP));

INSERT INTO bus_positions (bus_id, latitude, longitude, speed, recorded_at) VALUES
(1, 37.5559460, 126.9723170, 31.50, TIMESTAMPADD(MINUTE, -2, CURRENT_TIMESTAMP)),
(1, 37.5529400, 126.9695100, 28.00, TIMESTAMPADD(MINUTE, -6, CURRENT_TIMESTAMP)),
(2, 37.5662950, 126.9779450, 0.00, TIMESTAMPADD(MINUTE, -8, CURRENT_TIMESTAMP)),
(3, 37.5703770, 126.9918950, 42.30, TIMESTAMPADD(MINUTE, -1, CURRENT_TIMESTAMP)),
(4, 37.4979520, 127.0276190, 36.20, TIMESTAMPADD(MINUTE, -4, CURRENT_TIMESTAMP)),
(5, 37.5551230, 126.9368890, 18.70, TIMESTAMPADD(MINUTE, -12, CURRENT_TIMESTAMP));

INSERT INTO bus_events (bus_id, event_type, severity, description, latitude, longitude, occurred_at) VALUES
(3, 'SUDDEN_ACCELERATION', 'MEDIUM', '강남 방향 주행 중 급가속 이벤트가 감지되었습니다.', 37.5703770, 126.9918950, TIMESTAMPADD(MINUTE, -3, CURRENT_TIMESTAMP)),
(1, 'SUDDEN_STOP', 'LOW', '서울역 인근 정체 구간에서 급정거 이벤트가 감지되었습니다.', 37.5559460, 126.9723170, TIMESTAMPADD(MINUTE, -7, CURRENT_TIMESTAMP)),
(4, 'IMPACT', 'HIGH', '강남역 인근에서 충격 이벤트가 감지되었습니다.', 37.4979520, 127.0276190, TIMESTAMPADD(MINUTE, -15, CURRENT_TIMESTAMP)),
(5, 'SUDDEN_STOP', 'MEDIUM', '신촌오거리 접근 중 급정거 이벤트가 감지되었습니다.', 37.5551230, 126.9368890, TIMESTAMPADD(MINUTE, -18, CURRENT_TIMESTAMP));

INSERT INTO bus_camera_statuses (bus_id, camera_type, receiving, stream_url, last_received_at) VALUES
(1, 'FRONT', TRUE, 'mock://bus/7016-01/front', TIMESTAMPADD(MINUTE, -2, CURRENT_TIMESTAMP)),
(1, 'REAR', TRUE, 'mock://bus/7016-01/rear', TIMESTAMPADD(MINUTE, -2, CURRENT_TIMESTAMP)),
(1, 'INTERIOR_1', TRUE, 'mock://bus/7016-01/interior-1', TIMESTAMPADD(MINUTE, -2, CURRENT_TIMESTAMP)),
(1, 'INTERIOR_2', FALSE, NULL, TIMESTAMPADD(MINUTE, -9, CURRENT_TIMESTAMP)),
(2, 'FRONT', FALSE, NULL, TIMESTAMPADD(MINUTE, -8, CURRENT_TIMESTAMP)),
(3, 'FRONT', TRUE, 'mock://bus/143-01/front', TIMESTAMPADD(MINUTE, -1, CURRENT_TIMESTAMP)),
(3, 'INTERIOR_1', TRUE, 'mock://bus/143-01/interior-1', TIMESTAMPADD(MINUTE, -1, CURRENT_TIMESTAMP)),
(4, 'FRONT', TRUE, 'mock://bus/472-01/front', TIMESTAMPADD(MINUTE, -4, CURRENT_TIMESTAMP)),
(5, 'FRONT', FALSE, NULL, TIMESTAMPADD(MINUTE, -12, CURRENT_TIMESTAMP));
