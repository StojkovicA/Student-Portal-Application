package com.backend.controller.user.subject;

import com.backend.controller.user.subject.dto.SubjectW;
import com.backend.domain.subject.Subject;
import com.backend.domain.subscription.SubjectSubscription;
import com.backend.domain.user.User;
import com.backend.service.subject.SubjectService;
import com.backend.service.subject.subscription.SubjectSubscriptionService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.backend.utils.ContextHolderUtil.getUser;

@Validated
@RestController
@RequestMapping("/user/subject")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserSubjectController {

    private final SubjectService subjectService;
    private final SubjectSubscriptionService subjectSubscriptionService;

    @GetMapping("/getAllPossibleSubjects")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<SubjectW> getAllPossibleSubjects() {
        User user = getUser();
        List<Long> subscribedSubjects = subjectSubscriptionService.findAllByUserId(
                    user.getId(), ss -> Hibernate.initialize(ss.getSubject())
                ).stream()
                .map(SubjectSubscription::getSubject)
                .map(Subject::getId)
                .toList();

        return subjectService.getAllUserSubjects(user.getId())
                .stream()
                .map(subject -> new SubjectW(subject, subscribedSubjects.contains(subject.getId())))
                .toList();
    }

    @PostMapping("/subscribe/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void subscribe(@PathVariable @NotNull Long id) {
        User user = getUser();
        Subject subject = subjectService.findById(id);

        if(subjectSubscriptionService.isSubscribed(user.getId(), subject.getId())) {
            return;
        };

        SubjectSubscription subscription = new SubjectSubscription();
        subscription.setUser(user);
        subscription.setSubject(subject);
        subjectSubscriptionService.save(subscription);
    }

    @PostMapping("/unsubscribe/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void unsubscribe(@PathVariable @NotNull Long id) {
        User user = getUser();
        Subject subject = subjectService.findById(id);

        subjectSubscriptionService.findAllByUserId(user.getId(), null)
                .stream()
                .filter(ss -> Objects.equals(ss.getSubject().getId(), subject.getId()))
                .findFirst().ifPresent(subscription -> subjectSubscriptionService.deleteById(subscription.getId()));

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public SubjectW getSubjectWById(@PathVariable @NotNull Long id) {
        User user = getUser();
        Subject subject = subjectService.findById(id);

        boolean isSubscribed = subjectSubscriptionService.findAllByUserId(user.getId(), null)
                .stream()
                .anyMatch(subscription -> Objects.equals(subscription.getSubject().getId(), subject.getId()));

        return new SubjectW(subject, isSubscribed);
    }

    @GetMapping("/numberOfSubscribedUsers")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Long numberOfSubscribedUsers (@RequestParam Long subjectId){
        return subjectSubscriptionService.getNumberOfSubscribedUsers(subjectId);
    }

    @GetMapping("/listOfSubjectPosts")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<String> listOfSubjectPosts (@RequestParam Long subjectId){
        return subjectSubscriptionService.listOfSubjectPosts(subjectId);
    }
}
