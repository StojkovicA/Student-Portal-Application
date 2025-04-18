package com.backend.domain.token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tokens")
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Long id;
    @Column(length = 400)
    private String token;
    private String credentials;

    public Token(String token, String credentials) {
        this.token = token;
        this.credentials = credentials;
    }
}
