package com.backend.domain.subscription;

import com.backend.domain.user.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectSubscriptionRepository extends ListCrudRepository<SubjectSubscription, Long> {

    List<SubjectSubscription> findAllBySubjectId(Long id);

    List<SubjectSubscription> findAllByUserId(Long id);

    @Query(value = "SElECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM subscriptions  WHERE user_id = :userId AND subject_id = :subjectId", nativeQuery = true)
    long existsByUserAndSubject(Long userId, Long subjectId);

    @Query(value = "SELECT COUNT(*) FROM subscriptions s WHERE s.subject_id = :subjectId", nativeQuery = true)
    Long getNumberOfSubscribedUsers(Long subjectId);

}
