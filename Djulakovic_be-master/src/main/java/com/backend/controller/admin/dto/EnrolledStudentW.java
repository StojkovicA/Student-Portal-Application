package com.backend.controller.admin.dto;

import com.backend.domain.user.User;
import lombok.Getter;

@Getter
public class EnrolledStudentW {
    private final String name;
    private final String status;
    private final String indeks;
    private final String major;
    private final String module;
    private final String studiesType;
    public EnrolledStudentW(User user) {
        this.name = user.getFullName();
        this.status = user.getStatus().getDescription();
        this.indeks = user.getIndeks();
        this.module = user.getModule() == null
                ? ""
                : user.getModule().getModule();
        this.major = user.getMajor() == null
                ? ""
                : user.getMajor().getMajor();
        this.studiesType = user.getStudiesType() == null
                ? ""
                : user.getStudiesType().getType();
    }
}
