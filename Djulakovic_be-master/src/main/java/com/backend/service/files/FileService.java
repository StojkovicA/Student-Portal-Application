package com.backend.service.files;

import com.backend.domain.file.*;
import com.backend.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

import java.io.*;
import java.util.*;

import static java.io.File.separator;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.getProperty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FileService {

	private final FileUploadRepository fileUploadRepository;
	private final String appDirectory = getProperty("user.home") + separator + "uploads";

	public File storeFile(MultipartFile file, String subDirName, String fileName) throws IOException {
		File directory = getDir(subDirName);
		// File storeDir = isEmpty(subDirName) ? directoryMain : new File(directoryMain, subDirName);
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new IllegalStateException("Cannot create sub-directory");
			}
		}
		String name = isEmpty(fileName) ? valueOf(currentTimeMillis()) : fileName;
		File storeFile = new File(directory, name);
		file.transferTo(storeFile);
		return storeFile;
	}

	@Transactional
	public FileUploads createUploadFile(String fileName, String originalFilename, String filePath, long size, FileType fileType) {
		FileUploads fileUploads = new FileUploads();
		fileUploads.setUploadDate(System.currentTimeMillis());
		fileUploads.setFileName(fileName);
		fileUploads.setOriginalFileName(originalFilename);
		fileUploads.setFilePath(filePath);
		fileUploads.setSize(size);
		fileUploads.setFileType(fileType);

		return fileUploads;
	}

	@Transactional(readOnly = true)
	public List<FileUploads> findTopNByType(Integer number, FileType fileType) {
		return fileUploadRepository.findNByFileType(number, fileType.name());
	}

	private File getDir(String directory) {
		File mainDir = new File(getAppDirectory(),directory);
		if (!mainDir.exists()) {
			if (!mainDir.mkdirs()) {
				throw new IllegalStateException("Cannot create directory");
			}
		}
		return mainDir;
	}

	private File getAppDirectory() {
		File appDir = new File(appDirectory);
		if (!appDir.exists()) {
			if (!appDir.mkdirs()) {
				throw new IllegalStateException("Cannot find app directory");
			}
		}
		return appDir;
	}

	@Transactional
	public void deleteReel(Long id) {
		fileUploadRepository.deleteById(id);
	}
}
