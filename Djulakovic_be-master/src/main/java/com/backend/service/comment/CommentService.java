package com.backend.service.comment;

import com.backend.domain.comment.Comment;
import com.backend.domain.comment.CommentRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserPostRepository userPostRepository;
    private final NotificationsService notificationsService;

    @Transactional
    public void addComment(Long userId, Long userPostId, String comment_text){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        UserPost userPost = userPostRepository.findById(userPostId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid userPost id"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setUserPost(userPost);
        comment.setCommentText(comment_text);
        commentRepository.save(comment);

        notificationsService.createNotification(
                userPost.getUser().getFirebaseToken(),
                "Imate nov komentar od " + user.getFullName(),
                1L,
                FirebaseAction.ACTION_NEW_COMMENT
        );

    }

    @Transactional(readOnly = true)
    public List<Comment> getPostsWithComments(Long userPostId, Consumer<Comment> init){
        List<Comment> comments = commentRepository.findAllByCommentId(userPostId);

        if(init != null) {
            comments.forEach(init);
        }

        return comments;
    }

    @Transactional
    public void deleteComment(Long commentId){
        commentRepository.deleteById(commentId);
    }
}
