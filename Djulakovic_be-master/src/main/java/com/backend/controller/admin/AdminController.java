package com.backend.controller.admin;

import com.backend.controller.admin.dto.EnrolledStudentW;
import com.backend.controller.admin.dto.MajorW;
import com.backend.controller.admin.dto.SubjectW;
import com.backend.controller.admin.dto.UserW;
import com.backend.controller.admin.param.AddUserParam;
import com.backend.controller.admin.param.InsertParam;
import com.backend.controller.admin.param.SubjectParam;
import com.backend.controller.utils.PaginatedResponse;
import com.backend.domain.file.FileUploads;
import com.backend.domain.major.Major;
import com.backend.domain.module.Module;
import com.backend.domain.role.Roles;
import com.backend.domain.studies.type.StudiesType;
import com.backend.domain.subject.Subject;
import com.backend.domain.subscription.SubjectSubscription;
import com.backend.domain.user.Status;
import com.backend.domain.user.User;
import com.backend.service.*;
import com.backend.service.files.FileService;
import com.backend.service.major.MajorService;
import com.backend.service.module.ModuleService;
import com.backend.service.studies.type.StudiesTypeService;
import com.backend.service.subject.SubjectService;
import com.backend.service.subject.subscription.SubjectSubscriptionService;
import com.backend.service.user.UserReadService;
import com.backend.service.user.UserUpdateService;
import com.backend.utils.FileType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static com.backend.controller.utils.SearchUtils.DEFAULT_PASSWORD;
import static com.backend.controller.utils.SearchUtils.WILDCARD_SEARCH_TEMPLATE;
import static com.backend.domain.role.Role.ROLE_USER;
import static com.backend.utils.ContextHolderUtil.getUser;

@Validated
@RestController
@RequestMapping("/admin")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AdminController {

    private final MajorService majorService;
    private final ModuleService moduleService;
    private final SubjectService subjectService;
    private final PasswordEncoder passwordEncoder;
    private final UserReadService userReadService;
    private final UserUpdateService userUpdateService;
    private final StudiesTypeService studiesTypeService;
    private final SubjectSubscriptionService subjectSubscriptionService;
    private final FileService fileService;
    private final AwsService awsService;


    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PaginatedResponse<UserW, Void> getAll(@RequestParam int page,
                                                 @RequestParam int elements,
                                                 @RequestParam boolean asc,
                                                 @RequestParam @NotNull String sortColumn,
                                                 @RequestParam(defaultValue = "") String quickSearch) {
        Consumer<User> init = one -> {
            Hibernate.initialize(one.getMajor());
            Hibernate.initialize(one.getModule());
            Hibernate.initialize(one.getStudiesType());
            Hibernate.initialize(one.getRoles());
        };

        Page<User> content = userReadService.findPaginated
                (page, elements, sortColumn, asc, String.format(WILDCARD_SEARCH_TEMPLATE, quickSearch)
                        , init);

        return PaginatedResponse.<UserW, Void>builder()
                .data(content.get().map(UserW::new).toList())
                .totalElements(content.getTotalElements())
                .build();
    }

    /**
     *
     * This method adds user
     *
     * @param param user data
     */
    @PostMapping("/addUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addUser(@RequestBody @Valid AddUserParam param) {

        StudiesType type = studiesTypeService.findByType(param.getType());
        Major major = majorService.findByMajor(param.getMajor());
        Module module = moduleService.findByModule(param.getModule());
        Status status = Status.findByDescription(param.getStatus());

        User user = new User();
        user.setEmail(param.getEmail());
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setIndeks(param.getIndeks());
        user.setFirstName(param.getFirstName());
        user.setLastName(param.getLastName());
        user.setSignYear(param.getSignYear());
        user.setActive(true);
        user.setStatus(status);
        user.setStudiesType(type);
        user.setMajor(major);
        user.setModule(module);
        user.getRoles().add(new Roles(user, ROLE_USER));

        userUpdateService.save(user);
    }

    @PatchMapping("/changeActivity/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void changeActivity(@PathVariable @NotNull Long id) {
        userUpdateService.changeActivity(id);
    }

    @PostMapping("/addMajor")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addMajor(@RequestBody InsertParam param) {
        Major major = new Major();
        major.setMajor(param.getData());
        majorService.save(major);
    }

    @PostMapping("/addModule")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addModule(@RequestBody InsertParam param) {
        Module module = new Module();
        module.setModule(param.getData());
        moduleService.save(module);
    }

    @PostMapping("/addStudiesType")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addStudiesType(@RequestBody InsertParam param) {
        StudiesType studiesType = new StudiesType();
        studiesType.setType(param.getData());
        studiesTypeService.save(studiesType);
    }

    @GetMapping("/getMajors")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<MajorW> getMajors() {
        return majorService.findAll()
                .stream()
                .map(MajorW::new)
                .toList();
    }

    @PostMapping("/addSubject")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addSubject(@RequestBody SubjectParam param) {

        Major major = majorService.findById(param.getMajorId());
        Subject subject = new Subject();
        subject.setName(param.getName());
        subject.setYear(param.getYear());
        subject.setMajor(major);
        subjectService.save(subject);
    }

    @GetMapping("/getSubjects")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<SubjectW> getSubjects() {
        return subjectService.findAllInitialized(s -> Hibernate.initialize(s.getMajor())).stream()
                .map(SubjectW::new)
                .toList();
    }

    /**
     *
     * This method gets subject id
     *
     * @param id subject id
     * @return list of enrolled students to one subject
     */
    @GetMapping("/getEnrolledStudents/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<EnrolledStudentW> getEnrolledStudents(@PathVariable @NotNull Long id) {
        return subjectSubscriptionService.findSubjectSubscriptionsBySubjectId(id, s -> {
            Hibernate.initialize(s.getUser());
            if(s.getUser() != null) {
                Hibernate.initialize(s.getUser().getMajor());
                Hibernate.initialize(s.getUser().getModule());
                Hibernate.initialize(s.getUser().getStudiesType());
            }
        })
        .stream()
        .map(SubjectSubscription::getUser)
        .map(EnrolledStudentW::new)
        .toList();
    }

    @DeleteMapping("/deleteSubjects/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteSubject(@PathVariable @NotNull Long id) {
        subjectService.deleteSubjectById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/addReel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestPart("file") MultipartFile file,
                       @RequestParam(value = "fileName", required = false) String fileName) {


        try {
            File tempFile = File.createTempFile(file.getOriginalFilename(), "");
            file.transferTo(tempFile);
            String path = awsService.uploadFile(tempFile, FileType.ADMIN_REEL.getPath(), fileName);
            tempFile.delete(); // Clean up

            FileUploads fileUploads = fileService.createUploadFile(
                    fileName, file.getOriginalFilename(),
                    path,file.getSize(),
                    FileType.ADMIN_REEL
            );

            userUpdateService.uploadImage(getUser().getId(), fileUploads);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping("/deleteReel/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteReel(@PathVariable @NotNull Long id){
        fileService.deleteReel(id);
    }

}
