package com.backend.service.post;

import com.backend.domain.file.*;
import com.backend.domain.post.UserPost;
import com.backend.domain.post.UserPostRepository;
import com.backend.domain.subject.Subject;
import com.backend.domain.subject.SubjectRepository;
import com.backend.domain.subscription.*;
import com.backend.domain.user.User;
import com.backend.domain.user.UserRepository;
import com.backend.service.*;
import com.backend.service.subject.subscription.SubjectSubscriptionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserPostService {

    private final UserPostRepository userPostRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectSubscriptionService subjectSubscriptionService;
    private final NotificationsService notificationsService;


    @Transactional
    public UserPost addUserPost(Long userId, Long subjectId, String text){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject id"));

        if (!subjectSubscriptionService.isSubscribed(userId, subjectId)){
            throw new IllegalArgumentException("User is not subscribed to subject " + subject.getName());
        }

        UserPost userPost = new UserPost();
        userPost.setUser(user);
        userPost.setSubject(subject);
        userPost.setText(text);
        userPost.setPostDate(System.currentTimeMillis());
        UserPost saveUserPost =  userPostRepository.save(userPost);

        List<User> getAllSubscribedUsers = subjectSubscriptionService.findAllBySubjectId(subjectId)
                .stream()
                .map(SubjectSubscription::getUser)
                .filter(u -> Objects.equals(u.getId(), user.getId()))
                .toList();

        getAllSubscribedUsers.forEach(subUser -> notificationsService.createNotification(
                subUser.getFirebaseToken(),
                "Korisnik" + user.getFullName() + " je okacio novu objavu",
                1L,
                FirebaseAction.ACTION_NEW_POST)
        );

        return saveUserPost;
    }

    @Transactional
    public void addFileToPost(Long userPostId, FileUploads fileUploads) {
        UserPost userPost = userPostRepository.findById(userPostId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user post id"));

        userPost.getFiles()
                .add(fileUploads);
        userPostRepository.save(userPost);
    }

    @Transactional(readOnly = true)
    public List<UserPost> getPostBySubjectId(Long subjectId, Consumer<UserPost> init){
        List<UserPost> userPosts = userPostRepository.findAllBySubjectId(subjectId);

        if(init != null) {
            userPosts.forEach(init);
        }

        return userPosts;
    }

    @Transactional(readOnly = true)
    public List<UserPost> findTop10UserPosts(Long userId, Consumer<UserPost> init){
        List<UserPost> userPosts = userPostRepository.findTop10UserPosts(userId);

        if(init != null) {
            userPosts.forEach(init);
        }

        return userPosts;
    }

    @Transactional
    public List<UserPost> findTop10UserPostsAdmin(Consumer<UserPost> init){
        List<UserPost> userPosts = userPostRepository.findTop10UserPostsAdmin();

        if(init != null) {
            userPosts.forEach(init);
        }

        return userPosts;
    }

    @Transactional
    public void deleteUserPost(Long userPostId){
        userPostRepository.deleteById(userPostId);
    }
}
