package com.backend.service;

import lombok.*;

@Getter
@AllArgsConstructor
public enum FirebaseAction {

	ACTION_NEW_COMMENT("Comment Notification"),
	ACTION_NEW_LIKE("Like notification"),
	ACTION_NEW_POST("Post notification");

	private final String message;
}
