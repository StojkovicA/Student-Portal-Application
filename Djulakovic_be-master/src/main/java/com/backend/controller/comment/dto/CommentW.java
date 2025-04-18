package com.backend.controller.comment.dto;

import com.backend.domain.comment.Comment;
import lombok.Getter;

@Getter
public class CommentW {

    private final Long id;
    private final Long userId;
    private final String userName;
    private final Long userPostId;
    private final String comment;

    public CommentW(Comment comment){
        this.id = comment.getId();
        this.userId = comment.getUser().getId();
        this.userName = comment.getUser().getFullName();
        this.userPostId = comment.getUserPost().getId();
        this.comment = comment.getCommentText();
    }
}
