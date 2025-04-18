package com.backend.service;

import com.backend.domain.notification.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.function.*;

import static java.util.concurrent.TimeUnit.DAYS;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationsService {

	private final NotificationsRepository notificationsRepository;
	private final FirebaseService firebaseService;

	@Transactional(readOnly = true)
	public long countUnread(Set<Long> fleetsId) {
		return notificationsRepository.countByMarkedReadFalse();
	}

	@Transactional
	public void deleteAll(List<Long> ids) {
		notificationsRepository.deleteAllById(ids);
	}

	@Transactional(readOnly = true)
	public List<Notification> findAll() {
		return notificationsRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Notification findById(Long id, Consumer<Notification> init) {
		Notification notification = notificationsRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid notification id"));
		if (init != null) {
			init.accept(notification);
		}
		return notification;
	}

	@Transactional(readOnly = true)
	public List<Notification> getAll(Integer page, Integer elements, Set<Long> fleetsId, Consumer<Notification> init) {
		Long startTime = System.currentTimeMillis() - DAYS.toMillis(184);
		PageRequest pageRequest = PageRequest.of(page, elements, Sort.by("date")
				.descending());
		List<Notification> notifications = notificationsRepository.findAllDistinctByDateGreaterThan(startTime, pageRequest);
		if (init != null) {
			notifications.forEach(init);
		}
		return notifications;
	}

	@Transactional
	public void markAllAsRead(Set<Long> fleetsId) {
		List<Notification> notifications = notificationsRepository.findAllDistinctByFleetManagerIdInAndMarkedReadFalse(fleetsId);
		notifications.forEach(notification -> notification.setMarkedRead(true));
		saveAll(notifications);
	}

	@Transactional
	public void save(Notification notification) {
		notificationsRepository.save(notification);
	}

	@Transactional
	public void saveAll(List<Notification> notifications) {
		notificationsRepository.saveAll(notifications);
	}

	@Transactional
	public void createNotification(String token, String alertMessage, Long navigationId, FirebaseAction notAction) {

		AlertType alertType = AlertType.NOTIFICATION;
		Notification notification = new Notification();
		notification.setDate(System.currentTimeMillis());
		notification.setNavigationId(navigationId);
		notification.setMessage(alertMessage);
		notification.setAlertType(alertType);
		notificationsRepository.save(notification);
		firebaseService.sendFmMessage(token, notAction, alertType.getDescription(), alertMessage);

	}
}
