package com.backend.controller.user.subject.dto;


import com.backend.domain.subject.Subject;
import lombok.Getter;

@Getter
public class SubjectW {
    private final Long id;
    private final String subject;
    private final Long year;
    private final boolean subscribed;

    public SubjectW(Subject subject, boolean subscribed) {
        this.id = subject.getId();
        this.subject = subject.getName();
        this.year = subject.getYear();
        this.subscribed = subscribed;
    }
}
