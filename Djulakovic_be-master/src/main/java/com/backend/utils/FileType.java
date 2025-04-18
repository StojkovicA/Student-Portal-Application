package com.backend.utils;

import lombok.*;

import java.util.*;

@Getter
@AllArgsConstructor
public enum FileType {

	POST_PICTURE("postPicture", "posts"),
	PROFILE_PICTURE("profilePicture", "profile"),
	ADMIN_REEL("adminReel","reels");

	private final String type;
	private final String path;

	public FileType getByType(String type) {
		return Arrays.stream(FileType.values())
				.filter(fileType -> Objects.equals(fileType.getType(), type))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("No File type"));
	}
}
