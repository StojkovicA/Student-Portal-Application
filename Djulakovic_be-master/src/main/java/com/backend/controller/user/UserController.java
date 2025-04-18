package com.backend.controller.user;

import com.backend.controller.admin.dto.*;
import com.backend.domain.file.*;
import com.backend.domain.user.*;
import com.backend.service.*;
import com.backend.service.files.*;
import com.backend.service.user.*;
import com.backend.utils.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.backend.utils.ContextHolderUtil.getUser;

@Validated
@RestController
@RequestMapping("/user")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

	private final FileService fileService;
	private final UserUpdateService userUpdateService;
	private final AwsService awsService;

	@PreAuthorize("hasRole('ROLE_USER')")
	@ResponseStatus(value = HttpStatus.OK)
	@PostMapping(value = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String upload(@RequestParam(value = "fileName", required = false) String fileName,
			@RequestPart("file") MultipartFile file) throws IOException {

		try {
			File tempFile = File.createTempFile(file.getOriginalFilename(), "");
			file.transferTo(tempFile);
			String path = awsService.uploadFile(tempFile, FileType.PROFILE_PICTURE.getPath(), fileName);
			tempFile.delete(); // Clean up

			FileUploads fileUploads = fileService.createUploadFile(
					fileName, file.getOriginalFilename(),
					path,file.getSize(),
					FileType.PROFILE_PICTURE
			);
			userUpdateService.uploadImage(getUser().getId(), fileUploads);
			return "File uploaded successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to upload file: " + e.getMessage();
		}


	}

	@GetMapping("/getReels")
	@PreAuthorize("hasRole('ROLE_USER')")
	public List<FileW> getAll() {
		List<FileW> files = fileService.findTopNByType(100, FileType.ADMIN_REEL).stream().map(FileW::new).collect(Collectors.toList());
		return files;
	}

	@GetMapping("/firebase/{token}")
	@PreAuthorize("hasRole('ROLE_USER')")
	public void updateFirebaseToken(@PathVariable @NotNull String token) {

		User user = getUser();
		userUpdateService.updateUser(user.getId(), token);
	}
}
