package com.backend.controller.admin.dto;

import com.backend.domain.subject.Subject;
import lombok.Getter;

@Getter
public class SubjectW {

    private final Long id;
    private final String subject;
    private final Long year;

    public SubjectW(Subject subject) {
        this.id = subject.getId();
        this.subject = subject.getName();
        this.year = subject.getYear();
    }
}
