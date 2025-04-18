package com.backend.controller.admin.dto;

import com.backend.domain.major.Major;
import lombok.Getter;

@Getter
public class MajorW {
    private final Long id;
    private final String major;

    public MajorW(Major major) {
        this.id = major.getId();
        this.major = major.getMajor();
    }
}
