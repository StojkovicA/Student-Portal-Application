package com.backend.domain.major;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.service.annotation.GetExchange;

@Getter
@Setter
@Entity
@Table(name = "major")
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Long id;
    private String major;
}
