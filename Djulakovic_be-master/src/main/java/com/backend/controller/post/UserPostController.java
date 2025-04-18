package com.backend.controller.post;

import com.backend.controller.post.dto.UserPostW;
import com.backend.controller.post.param.UserPostParam;
import com.backend.domain.file.FileUploads;
import com.backend.domain.post.UserPost;
import com.backend.domain.role.Role;
import com.backend.domain.user.User;
import com.backend.service.*;
import com.backend.service.files.FileService;
import com.backend.service.post.UserPostService;
import com.backend.utils.FileType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static com.backend.utils.ContextHolderUtil.getUser;

@RestController
@RequestMapping(value = "/user/post")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserPostController {

    private final UserPostService userPostService;
    private final FileService fileService;
    private final AwsService awsService;

    @PostMapping(value = "/addUserPost")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Long addUserPost(@RequestBody UserPostParam param) {
        UserPost userPost = userPostService.addUserPost(param.getUserId(), param.getSubjectId(), param.getText());
        return userPost.getId();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/addFileToPost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestPart("file") MultipartFile file,
                       @RequestParam(value = "fileName", required = false) String fileName,
                       @RequestParam("id") Long id) {

        try {
            File tempFile = File.createTempFile(file.getOriginalFilename(), "");
            file.transferTo(tempFile);
            String path = awsService.uploadFile(tempFile, FileType.POST_PICTURE.getPath(), fileName);
            tempFile.delete(); // Clean up

            FileUploads fileUploads = fileService.createUploadFile(
                    fileName, file.getOriginalFilename(),
                    path,file.getSize(),
                    FileType.POST_PICTURE
            );
            userPostService.addFileToPost(id, fileUploads);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @GetMapping(value = "/getPostBySubjectId")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<UserPostW> getPostBySubjectId (@RequestParam Long subjectId){

        Consumer<UserPost> init = one -> {
            Hibernate.initialize(one.getUser().getModule());
            Hibernate.initialize(one.getSubject());
            Hibernate.initialize(one.getLikes());
            Hibernate.initialize(one.getComments());
            Hibernate.initialize(one.getFiles());
            Hibernate.initialize(one.getUser().getFiles());
        };

        return userPostService.getPostBySubjectId(subjectId,init).stream()
                .map(UserPostW::new)
                .toList();
    }

    @GetMapping(value = "/latest")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public List<UserPostW> findTop10UserPosts(){

        Consumer<UserPost> init = one -> {
            Hibernate.initialize(one.getUser().getModule());
            Hibernate.initialize(one.getSubject());
            Hibernate.initialize(one.getLikes());
            Hibernate.initialize(one.getComments());
            Hibernate.initialize(one.getFiles());
            Hibernate.initialize(one.getUser().getFiles());
        };

        User user = getUser();

        List<UserPost> userPosts;

        if(user.getRoles().stream().anyMatch(Roles -> Roles.getRole() == Role.ROLE_ADMIN)){
            userPosts = userPostService.findTop10UserPostsAdmin(init);
        }else{
            userPosts = userPostService.findTop10UserPosts(user.getId(), init);
        }

        return userPosts.stream()
                .map(UserPostW::new)
                .toList();
    }

    @DeleteMapping(value = "/deletePost/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deletePost(@PathVariable @NotNull Long id){
        userPostService.deleteUserPost(id);
    }

    @DeleteMapping(value = "/deletePostAdmin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deletePostAdmin(@PathVariable @NotNull Long id){
        userPostService.deleteUserPost(id);
    }
}
