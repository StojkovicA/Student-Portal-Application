package com.backend.domain.subject;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends ListCrudRepository<Subject, Long> {
    List<Subject> findAllByMajorId(Long id);

}
