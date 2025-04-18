package com.backend.controller.like.param;

import lombok.Getter;

@Getter
public class LikeParam {

    private Long userId;
    private Long userPostId;
    private boolean liked;
}
