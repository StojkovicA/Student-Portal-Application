package com.backend.controller.like.dto;

import com.backend.domain.file.*;
import com.backend.domain.like.Like;
import com.backend.utils.*;
import lombok.Getter;

import java.util.*;

import static java.util.Objects.isNull;

@Getter
public class LikeSimpleW {

	private final Long id;
	private final Long userId;
	private final String userName;

	public LikeSimpleW(Like like){
		this.id = like.getId();
		this.userId = like.getUser().getId();
		this.userName = like.getUser().getFullName();
	}
}
