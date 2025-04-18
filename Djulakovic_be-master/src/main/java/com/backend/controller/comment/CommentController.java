package com.backend.controller.comment;


import com.backend.controller.comment.dto.CommentW;
import com.backend.controller.comment.param.CommentParam;
import com.backend.domain.comment.Comment;
import com.backend.service.comment.CommentService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Consumer;

@RestController
@RequestMapping(value = "/user/comment")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CommentController {

    private final CommentService commentService;

    @PostMapping(value = "/addComment")
    @PreAuthorize("hasRole('ROLE_USER')")
    public  void addComment(@RequestBody CommentParam commentParam){
        commentService.addComment(commentParam.getUserId(), commentParam.getUserPostId(), commentParam.getCommented());
    }

    @GetMapping(value = "/getPostsWithComments")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<CommentW> getPostsWithComments(@RequestParam Long userPostId){

        Consumer<Comment> init = one -> {
            Hibernate.initialize(one.getUser());
            Hibernate.initialize(one.getUserPost());
        };

        return commentService.getPostsWithComments(userPostId,init).stream()
                .map(CommentW::new)
                .toList();
    }

    @DeleteMapping(value = "/deleteComment/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deleteComment (@PathVariable @NotNull Long id){
        commentService.deleteComment(id);
    }

    @DeleteMapping(value = "/deleteCommentAdmin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCommentAdmin (@PathVariable @NotNull Long id){
        commentService.deleteComment(id);
    }

}
