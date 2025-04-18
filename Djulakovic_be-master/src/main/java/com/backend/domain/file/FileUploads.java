package com.backend.domain.file;

import com.backend.utils.*;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "files")
public class FileUploads {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	private Long id;

	@Column(name = "file_path")
	private String filePath;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "original_file_name")
	private String originalFileName;

	@Column(name = "size")
	private Long size;

	@Column(name = "create_at")
	private Long uploadDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "file_type", nullable = false)
	private FileType fileType;

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
		return id.equals(((FileUploads) obj).id);
	}

	@Override
	public int hashCode() {
		return 42;
	}

}
