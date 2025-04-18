package com.backend.controller.admin.dto;

import com.backend.domain.file.*;
import lombok.*;

@Data
public class FileW {

	private final Long id;
	private final String path;
	private final String fileName;

	public FileW(FileUploads fileUploads) {
		this.id = fileUploads.getId();
		this.path = fileUploads.getFilePath();
		this.fileName = fileUploads.getFileName();
	}
}
