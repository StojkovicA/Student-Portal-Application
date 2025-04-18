package com.backend.domain.role;

import com.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
@NoArgsConstructor
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(columnDefinition = "VARCHAR(32)")
    @Enumerated(EnumType.STRING)
    private Role role;

    public Roles(User user, Role role) {
        this.user = user;
        this.role = role;
    }
}
