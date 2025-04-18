package com.backend.service.user;

import com.backend.domain.file.*;
import com.backend.domain.post.*;
import com.backend.domain.user.User;
import com.backend.domain.user.UserRepository;
import com.backend.utils.ContextHolderUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.backend.controller.utils.SearchUtils.DEFAULT_PASSWORD;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserUpdateService {

    private final UserRepository userRepository;
    private final UserReadService userReadService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void saveAll(List<User> users) {
        userRepository.saveAll(users);
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void changeActivity(Long id) {
        User user = userReadService.findById(id);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword() {
        User user = userReadService.findById(ContextHolderUtil.getUserDetails().getId());
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(String newPassword) {
        User user = userReadService.findById(ContextHolderUtil.getUserDetails().getId());
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void uploadImage(Long userId, FileUploads fileUpload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        user.getFiles()
                .add(fileUpload);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long userId, String token) {
        userRepository.updateFirebaseToken(userId, token);
    }
}
