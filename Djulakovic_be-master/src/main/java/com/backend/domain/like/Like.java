package com.backend.domain.like;

import com.backend.domain.post.UserPost;
import com.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_post_likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Long id;

    @Column(name = "liked")
    private boolean liked;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_post_id")
    private UserPost userPost;
}
