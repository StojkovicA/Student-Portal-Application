package com.backend.service.user;

import com.backend.domain.user.User;
import com.backend.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Transactional
public class UserReadService {
    private final UserRepository userRepository;

    @Autowired
    public UserReadService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email, Consumer<User> init) {
        User user = userRepository.findByEmail(email);
        if(user != null && init != null) {
            init.accept(user);
        }
        return user;
    }

    @Transactional
    public Page<User> findPaginated(int page, int elementCount,
                                    String sortColumn, boolean asc,
                                    String quickSearch, Consumer<User> init) {
        Pageable pageRequest = PageRequest.of(page - 1, elementCount, Sort.by(asc ? ASC : DESC, sortColumn));
        Page<User> users = userRepository.findAllDistinctByQuickSearch(quickSearch, pageRequest);
        if(init != null) {
            users.get().forEach(init);
        }

        return users;
    }

    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with such ID"));
    }

    @Transactional
    public User findByIdInitialized(Long id, Consumer<User> init) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with such ID"));

       init.accept(user);

        return user;
    }
}
