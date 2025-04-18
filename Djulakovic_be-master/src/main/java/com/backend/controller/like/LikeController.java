package com.backend.controller.like;

import com.backend.controller.like.dto.LikeW;
import com.backend.controller.like.param.LikeParam;
import com.backend.domain.like.Like;
import com.backend.service.like.LikeService;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Consumer;

@RestController
@RequestMapping(value = "/user/like")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LikeController {

    private final LikeService likeService;

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    public void likePost(@RequestBody LikeParam param){
        likeService.likePost(param.getUserId(), param.getUserPostId(), param.isLiked());
    }

    @GetMapping(value = "/getPostsWithLikes")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<LikeW> getPostsWithLikes(@RequestParam Long userPostId){

        Consumer<Like> init = one -> {
            Hibernate.initialize(one.getUser());
            Hibernate.initialize(one.getUserPost());
        };

        return likeService.getPostsWithLikes(userPostId,init).stream()
                .map(LikeW::new)
                .toList();
    }
}
