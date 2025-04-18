package com.backend.service.subject.subscription;

import com.backend.domain.post.UserPostRepository;
import com.backend.domain.subject.*;
import com.backend.domain.subscription.SubjectSubscription;
import com.backend.domain.subscription.SubjectSubscriptionRepository;
import com.backend.domain.user.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SubjectSubscriptionService {
    private final SubjectSubscriptionRepository subjectSubscriptionRepository;
    private final UserPostRepository userPostRepository;

    @Transactional
    public List<SubjectSubscription> findSubjectSubscriptionsBySubjectId(Long id, Consumer<SubjectSubscription> init) {
        List<SubjectSubscription> subjectSubscriptions = subjectSubscriptionRepository.findAllBySubjectId(id);
        if(init != null) {
            subjectSubscriptions.forEach(init);
        }
        return subjectSubscriptions;
    }

    @Transactional
    public List<SubjectSubscription> findAllByUserId(Long id, Consumer<SubjectSubscription> init) {
        return subjectSubscriptionRepository.findAllByUserId(id);
    }

    public boolean isSubscribed(Long userId, Long subjectId){
        return subjectSubscriptionRepository.existsByUserAndSubject(userId, subjectId) > 0;
    }

    public void save(SubjectSubscription subscription) {
        subjectSubscriptionRepository.save(subscription);
    }

    public void deleteById(Long id) {
        subjectSubscriptionRepository.deleteById(id);
    }

    @Transactional
    public Long getNumberOfSubscribedUsers(Long subjectId){
        return subjectSubscriptionRepository.getNumberOfSubscribedUsers(subjectId);
    }


    @Transactional
    public List<String> listOfSubjectPosts(Long subjectId){
        return userPostRepository.listOfSubjectPosts(subjectId);
    }

    @Transactional
    public List<SubjectSubscription> findAllBySubjectId(Long subjectId) {
        return subjectSubscriptionRepository.findAllBySubjectId(subjectId);
    }

}
