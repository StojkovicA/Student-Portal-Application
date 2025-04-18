package com.backend.service.major;

import com.backend.domain.major.Major;
import com.backend.domain.major.MajorRepository;
import com.backend.domain.module.Module;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MajorService {

    private final MajorRepository majorRepository;

    @Transactional
    public Major findByMajor(String major) {
        return majorRepository.findFirstByMajor(major);
    }

    @Transactional
    public Set<Major> findAll() {
        return new HashSet<>(majorRepository.findAll());
    }

    @Transactional
    public void save(Major major) {
        majorRepository.save(major);
    }

    @Transactional
    public void saveAll(Collection<Major> majors) {
        majorRepository.saveAll(majors);
    }

    @Transactional
    public Major findById(Long majorId) {
        return majorRepository.findById(majorId).orElseThrow(() -> new IllegalArgumentException("Invalid Major ID"));
    }
}
