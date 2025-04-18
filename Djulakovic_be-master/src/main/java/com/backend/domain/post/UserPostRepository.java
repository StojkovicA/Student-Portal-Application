package com.backend.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPostRepository extends JpaRepository<UserPost, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM user_post WHERE subject_id = :subjectId")
    List<UserPost> findAllBySubjectId(Long subjectId);

    @Query(nativeQuery = true, value = "SELECT up.* " +
            "FROM user_post up JOIN subscriptions s on s.subject_id = up.subject_id  " +
            "WHERE s.user_id  = :userId " +
            "ORDER BY post_date DESC LIMIT 10; ")
    List<UserPost> findTop10UserPosts(Long userId);

    @Query(value = "SELECT * FROM user_post ORDER BY post_date DESC LIMIT 10", nativeQuery = true)
    List<UserPost> findTop10UserPostsAdmin();

    @Query(value = "SELECT up.text FROM user_post up  WHERE up.subject_id = :subjectId", nativeQuery = true)
    List<String> listOfSubjectPosts(Long subjectId);

}
