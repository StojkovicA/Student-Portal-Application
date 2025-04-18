package com.backend.domain.migration;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "migrations")
public class Migration {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Long id;
    private boolean initializedUsers;
    private boolean createdGodUser;
}
