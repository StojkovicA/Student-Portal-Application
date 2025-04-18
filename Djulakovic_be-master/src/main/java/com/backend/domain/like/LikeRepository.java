package com.backend.domain.like;

import com.backend.domain.post.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM user_post_likes WHERE user_post_id = :userPostId")
    List<Like> findAllByLikedId(Long userPostId);

    @Query(nativeQuery = true, value = "SELECT * FROM user_post_likes WHERE user_post_id = :userPostId and user_id = :userId")
    List<Like> findAllByUserAndUserPost(Long userId, Long userPostId);


    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM user_post_likes WHERE user_post_id = :userPostId and user_id = :userId")
    void dislike(Long userId, Long userPostId);

}
