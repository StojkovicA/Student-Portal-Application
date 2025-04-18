package com.backend.domain.notification;

import jakarta.persistence.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface NotificationsRepository extends ListCrudRepository<Notification, Long> {

	long countByMarkedReadFalse();

	@Modifying
	@QueryHints(@QueryHint(name = "jakarta.persistence.query.timeout", value = "90000000"))
	@Query(value = "DELETE FROM notifications WHERE id in :ids", nativeQuery = true)
	void deleteAllById(List<Long> ids);


	List<Notification> findAllDistinctByDateGreaterThan(Long startTime, Pageable pageable);

	@Query(value = "SELECT DISTINCT * FROM  notifications " +
			"WHERE fleetManager_id IN :fleetManagersId AND markedRead = false", nativeQuery = true)
	List<Notification> findAllDistinctByFleetManagerIdInAndMarkedReadFalse(Collection<Long> fleetManagersId);
}
