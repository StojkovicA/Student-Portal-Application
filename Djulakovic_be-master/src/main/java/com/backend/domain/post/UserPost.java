package com.backend.domain.post;

import com.backend.domain.comment.*;
import com.backend.domain.file.*;
import com.backend.domain.like.Like;
import com.backend.domain.subject.Subject;
import com.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@Table(name = "user_post")
public class UserPost {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Long id;

    @Column(name = "text", columnDefinition = "VARCHAR(500)")
    private String text;

    @Column(name = "post_date")
    private Long postDate;

    @JoinTable(name = "user_post_files")
    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    private List<FileUploads> files;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userPost", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Like> likes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userPost", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments;

    public UserPost() {
        this.files = new ArrayList<>();
    }

}
