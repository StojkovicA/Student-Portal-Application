package com.backend.domain.notification;

import lombok.*;

@Getter
@AllArgsConstructor
public enum NotificationType {

	HOS("hos"),
	LOG_EDIT("log"),
	DVIR("dvir"),
	SPEED_LIMIT("speed_limit"),
	FAULT_CODE("fault_code"),
	GEOFENCE_EXIT("geofenceExit"),
	GEOFENCE_ENTER("geofenceEnter"),
	GEOFENCE_ENTRY_EXIT("geofenceEntryExit");
	private final String id;

}