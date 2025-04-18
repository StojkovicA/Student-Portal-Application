package com.backend.domain.notification;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@Table(name = "notifications", indexes = {
		@Index(columnList = "date DESC", name = "date_index"),
		@Index(columnList = "markedRead", name = "marked_read_index")}
)
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@Column(name = "id")
	private Long id;

	@Enumerated(STRING)
	@Deprecated(since = "3.0.0")
	private NotificationType type;
	@Enumerated(STRING)
	private AlertType alertType;
	private String message;
	private boolean markedRead;
	private long date;
	private Long navigationId;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		if (id == null) {
			return false;
		}
		return id.equals(((Notification) obj).id);
	}

	@Override
	public int hashCode() {
		return 42;
	}
}
