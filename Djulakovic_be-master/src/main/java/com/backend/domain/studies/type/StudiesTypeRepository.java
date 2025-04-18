package com.backend.domain.studies.type;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Set;

public interface StudiesTypeRepository extends ListCrudRepository<StudiesType, Long> {

    StudiesType findFirstByType(String studiesType);
}

