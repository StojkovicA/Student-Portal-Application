package com.backend.service.module;

import com.backend.domain.major.Major;
import com.backend.domain.major.MajorRepository;
import com.backend.domain.module.Module;
import com.backend.domain.module.ModuleRepository;
import com.backend.domain.studies.type.StudiesType;
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
public class ModuleService {

    private final ModuleRepository moduleRepository;

    @Transactional
    public Module findByModule(String module) {
        return moduleRepository.findFirstByModule(module);
    }

    @Transactional
    public Set<Module> findAll() {
        return new HashSet<>(moduleRepository.findAll());
    }

    @Transactional
    public void save(Module module) {
        moduleRepository.save(module);
    }

    @Transactional
    public void saveAll(Collection<Module> modules) {
        moduleRepository.saveAll(modules);
    }
}
