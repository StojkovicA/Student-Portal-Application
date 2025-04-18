package com.backend.controller.admin.dto;

import com.backend.domain.file.*;
import com.backend.domain.user.User;
import com.backend.utils.*;
import lombok.Getter;

import java.util.*;
import java.util.stream.*;

@Getter
public class UserW {

    private final Long id;
    private final String email;
    private final String index;
    private final String fullName;
    private final Long signYear;
    private final Boolean active;
    private final String studiesType;
    private final String major;
    private final String module;
    private final String status;
    private final String profilePicture;

    public UserW(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.index = user.getIndeks();
        this.fullName = user.getFullName();
        this.signYear = user.getSignYear();
        this.active = user.isActive();

        this.studiesType = user.getStudiesType() == null
                ? ""
                : user.getStudiesType().getType();
        this.major = user.getMajor() == null
                ? ""
                : user.getMajor().getMajor();
        this.module = user.getModule() == null
                ? ""
                : user.getModule().getModule();
        this.status = user.getStatus() == null
                ? ""
                : user.getStatus().getDescription();

        if(user.getFiles() == null || user.getFiles().size() == 0) {
            this.profilePicture = null;
        } else {
            user.getFiles()
                    .sort(Comparator.comparing(FileUploads::getUploadDate));
            this.profilePicture = user.getFiles()
                    .stream()
                    .filter(fu -> fu.getFileType().equals(FileType.PROFILE_PICTURE))
                    .map(FileUploads::getFilePath)
                    .findFirst().orElse(null);
        }

    }
}
