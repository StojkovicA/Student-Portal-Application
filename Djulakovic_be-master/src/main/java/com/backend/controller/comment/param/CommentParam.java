package com.backend.controller.comment.param;

import lombok.Getter;

@Getter
public class CommentParam {

    private Long userId;
    private Long userPostId;
    private String commented;
}
