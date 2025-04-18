package com.backend.controller.user.auth;

import com.backend.controller.admin.dto.UserW;
import com.backend.domain.user.User;
import com.backend.service.user.UserReadService;
import com.backend.service.user.UserUpdateService;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Consumer;

import static com.backend.utils.ContextHolderUtil.getUser;

@Validated
@RestController
@RequestMapping("/user/authentication")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationController {

    private final UserReadService userReadService;
    private final UserUpdateService userUpdateService;

    @PostMapping("/resetPassword")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void resetPassword() {
        userUpdateService.resetPassword();
    }

    @PostMapping("/changePassword")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void changePassword(@RequestParam String newPassword) {
        userUpdateService.changePassword(newPassword);
    }

    @GetMapping("/getDetails")
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserW getUserDetails() {
        Consumer<User> init = one -> {
            Hibernate.initialize(one.getStudiesType());
            Hibernate.initialize(one.getMajor());
            Hibernate.initialize(one.getModule());
            Hibernate.initialize(one.getStatus());
            Hibernate.initialize(one.getFiles());
        };

        User user = getUser();

        return new UserW(userReadService.findByIdInitialized(user.getId(), init));
    }


}
