package com.backend.service.subject;

import com.backend.domain.subject.Subject;
import com.backend.domain.subject.SubjectRepository;
import com.backend.domain.user.User;
import com.backend.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Transactional
    public void save(Subject subject) {
        subjectRepository.save(subject);
    }

    @Transactional
    public List<Subject> findAllInitialized(Consumer<Subject> init) {
        List<Subject> subjects = subjectRepository.findAll();
        if(init != null) {
            subjects.forEach(init);
        }
        return subjects;
    }

    @Transactional
    public void deleteSubjectById(Long id) {
        subjectRepository.deleteById(id);
    }

    @Transactional
    public List<Subject> getAllUserSubjects(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("No user found")
        );
        return subjectRepository.findAllByMajorId(user.getMajor().getId());
    }

    @Transactional
    public Subject findById(Long id) {
        return subjectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No such subject"));
    }


}
