package com.backend.domain.notification;

import lombok.*;

import java.util.*;

import static java.util.Arrays.asList;

@Getter
@AllArgsConstructor
public enum AlertType {
	//Geofence section
	NOTIFICATION("notification", "notification", Collections.emptyList(), "");

	private final String id;
	private final String description;
	private final List<String> triggers;
	private final String imageUrl;

	public static AlertType getById(String id) {
		return Arrays.stream(AlertType.values())
				.filter(a -> a.getId()
						.equals(id))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid alert type"));
	}
	public String getId() {
		return id;
	}
}

