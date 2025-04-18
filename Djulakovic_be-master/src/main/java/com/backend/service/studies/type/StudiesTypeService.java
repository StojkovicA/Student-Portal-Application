package com.backend.service.studies.type;

import com.backend.domain.module.Module;
import com.backend.domain.module.ModuleRepository;
import com.backend.domain.studies.type.StudiesType;
import com.backend.domain.studies.type.StudiesTypeRepository;
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
public class StudiesTypeService {

    private final StudiesTypeRepository studiesTypeRepository;
    @Transactional
    public StudiesType findByType(String type) {
        return studiesTypeRepository.findFirstByType(type);
    }

    @Transactional
    public Set<StudiesType> findAll() {
        return new HashSet<>(studiesTypeRepository.findAll());
    }

    @Transactional
    public void save(StudiesType type) {
        studiesTypeRepository.save(type);
    }

    public void saveAll(Collection<StudiesType> studiesTypes) {
        studiesTypeRepository.saveAll(studiesTypes);
    }
}
