package com.backend.controller.admin.param;

import com.backend.controller.admin.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@UniqueEmail
public class AddUserParam {

    //@Email
    //@NotNull
    private String email;
    //@NotNull
    private String indeks;
    //@NotNull
    private String firstName;
    //@NotNull
    private String lastName;
    //@NotNull
    private Long signYear;
    //@NotNull
    private String type;
    //@NotNull
    private String major;
    //@NotNull
    private String module;
    //@NotNull
    private String status;
}
