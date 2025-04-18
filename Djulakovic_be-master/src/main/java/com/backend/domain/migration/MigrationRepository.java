package com.backend.domain.migration;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MigrationRepository extends ListCrudRepository<Migration, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM migrations LIMIT 1")
    Migration findFirst();
}
