package com.backend.domain.subject;

import com.backend.domain.major.Major;
import com.backend.domain.post.UserPost;
import com.backend.domain.subscription.SubjectSubscription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Long id;

    private String name;

    private Long year;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "major_id")
    private Major major;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SubjectSubscription> subjectSubscriptions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserPost> userPosts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject subject)) return false;
        return Objects.equals(getId(), subject.getId());
    }

    @Override
    public int hashCode() {
        return 24;
    }
}
