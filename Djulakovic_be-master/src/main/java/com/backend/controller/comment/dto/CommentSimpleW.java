package com.backend.controller.comment.dto;

import com.backend.domain.comment.Comment;
import com.backend.domain.file.*;
import com.backend.utils.*;
import lombok.Getter;

import java.util.*;

import static java.util.Objects.isNull;

@Getter
public class CommentSimpleW {
	private final Long id;
	private final Long userId;
	private final String userName;
	private final String commented;

	public CommentSimpleW(Comment comment){
		this.id = comment.getId();
		this.userId = comment.getUser().getId();
		this.userName = comment.getUser().getFullName();
		this.commented = comment.getCommentText();
	}
}
