package com.backend.domain.file;

import com.backend.utils.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUploads, Long> {
	@Query(nativeQuery = true, value = "SELECT * FROM files " +
			" WHERE file_type = :fileType ORDER BY create_at LIMIT :number")
	List<FileUploads> findNByFileType(Integer number, String fileType);
}
