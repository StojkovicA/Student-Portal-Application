package com.backend.service.like;

import com.backend.domain.like.Like;
import com.backend.domain.like.LikeRepository;
import com.backend.domain.post.UserPost;
import com.backend.domain.post.UserPostRepository;
import com.backend.domain.user.User;
import com.backend.domain.user.UserRepository;
import com.backend.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final UserPostRepository userPostRepository;
    private final NotificationsService notificationsService;

    @Transactional
    public void likePost(Long userId, Long userPostId, Boolean liked){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        UserPost userPost = userPostRepository.findById(userPostId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid userPost id"));

        if(isLiked(user.getId(), userPost.getId())) {
            likeRepository.dislike(user.getId(), userPost.getId());
            return;
        }

        Like like = new Like();
        like.setUser(user);
        like.setUserPost(userPost);
        like.setLiked(liked);
        likeRepository.save(like);

        notificationsService.createNotification(
                userPost.getUser().getFirebaseToken(),
                "Korisnik" + user.getFullName() + " je lajkovao vasu objavu",
                1L,
                FirebaseAction.ACTION_NEW_LIKE
        );
    }

    private boolean isLiked(Long userId, Long userPostId) {
        return likeRepository.findAllByUserAndUserPost(userId, userPostId).size() > 0;
    }

    @Transactional(readOnly = true)
    public List<Like>  getPostsWithLikes(Long userPostId, Consumer<Like> init){
        List<Like> likes = likeRepository.findAllByLikedId(userPostId);

        if(init != null) {
            likes.forEach(init);
        }

        return likes;
    }
}
