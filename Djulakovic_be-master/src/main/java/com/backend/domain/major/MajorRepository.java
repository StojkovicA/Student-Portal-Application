package com.backend.domain.major;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MajorRepository extends ListCrudRepository<Major, Long> {

    Major findFirstByMajor(String major);
}
