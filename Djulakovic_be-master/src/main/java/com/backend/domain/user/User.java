package com.backend.domain.user;

import com.backend.domain.file.*;
import com.backend.domain.like.Like;
import com.backend.domain.major.Major;
import com.backend.domain.module.Module;
import com.backend.domain.post.UserPost;
import com.backend.domain.role.Role;
import com.backend.domain.role.Roles;
import com.backend.domain.studies.type.StudiesType;
import com.backend.domain.subscription.SubjectSubscription;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Long id;
    @Column(unique = true, nullable = false)
    //@Email
    private String email;
    private String password;
    private String indeks;
    private String firstName;
    private String lastName;
    private Long signYear;
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studiesType_id")
    private StudiesType studiesType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Roles> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SubjectSubscription> subjectSubscriptions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserPost> userPosts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Like> likes;

    @Column(name="firebase_token")
    private String firebaseToken;

    @JoinTable(name = "user_images")
    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    private List<FileUploads> files;

    @Enumerated(EnumType.STRING)
    private Status status;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public User(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.active = user.isActive();
        this.roles = user.getRoles();
    }

    public User() {
        this.roles = new ArrayList<>();
    }

    public List<Role> getActualRoles() {
       return roles.stream()
               .map(Roles::getRole)
               .toList();
    }
}
