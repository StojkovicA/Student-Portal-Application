package com.backend.controller.like.dto;

import com.backend.domain.like.Like;
import lombok.Getter;

@Getter
public class LikeW {

    private final Long id;
    private final Long userId;
    private final String userName;
    private final Long userPostId;
    private final String postText;

    public LikeW(Like like){
        this.id = like.getId();
        this.userId = like.getUser().getId();
        this.userName = like.getUser().getFullName();
        this.userPostId = like.getUserPost().getId();
        this.postText = like.getUserPost().getText();
    }
}
