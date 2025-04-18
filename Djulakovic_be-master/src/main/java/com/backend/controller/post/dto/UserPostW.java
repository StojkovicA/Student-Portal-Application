package com.backend.controller.post.dto;

import com.backend.controller.comment.dto.*;
import com.backend.controller.like.dto.*;
import com.backend.domain.file.*;
import com.backend.domain.post.UserPost;
import com.backend.utils.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.*;

import static io.jsonwebtoken.lang.Collections.isEmpty;
import static java.util.Objects.isNull;


@Getter
public class UserPostW {

    private static final Logger log = LoggerFactory.getLogger(UserPostW.class);
    private final Long id;
    private final Long userId;
    private final String userName;
    private final String profilePicture;
    private final Long subjectId;
    private final String subjectName;
    private final String text;
    private final Long postDate;
    private final String module;
    private final List<LikeSimpleW> likes;
    private final List<CommentSimpleW> comments;
    private final List<String> filePaths;

    public UserPostW(UserPost userPost){
        this.id = userPost.getId();
        this.userId = userPost.getUser().getId();
        this.userName = userPost.getUser().getFullName();
        this.profilePicture = isNull(userPost.getUser().getFiles())
                               ? "no picture"
                               : userPost.getUser().getFiles().stream()
                                      .filter(fileUploads -> Objects.equals(fileUploads.getFileType(), FileType.PROFILE_PICTURE))
                                      .map(FileUploads::getFilePath)
                                      .findFirst()
                                      .orElse(null);
        this.module = userPost.getUser().getModule().getModule();
        this.subjectId = userPost.getSubject().getId();
        this.subjectName = userPost.getSubject().getName();
        this.text = userPost.getText();
        this.postDate = userPost.getPostDate();
        this.filePaths = isNull(userPost.getFiles())
                         ? Collections.emptyList()
                         : userPost.getFiles().stream().filter(fileUploads -> Objects.equals(fileUploads.getFileType(), FileType.POST_PICTURE))
                                 .map(FileUploads::getFilePath).collect(Collectors.toList());
        this.likes = isNull(userPost.getLikes())  ? Collections.emptyList() : userPost.getLikes().stream().map(LikeSimpleW::new).collect(Collectors.toList());
        this.comments = isNull(userPost.getComments()) ? Collections.emptyList() : userPost.getComments().stream().map(CommentSimpleW::new).collect(Collectors.toList());
    }

}
