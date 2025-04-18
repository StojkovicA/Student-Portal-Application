package com.backend.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM user_post_comments WHERE user_post_id = :userPostId")
    List<Comment> findAllByCommentId(Long userPostId);
}
