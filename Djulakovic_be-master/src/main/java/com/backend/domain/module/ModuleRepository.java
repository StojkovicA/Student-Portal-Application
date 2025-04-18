package com.backend.domain.module;

import com.backend.domain.major.Major;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Set;

public interface ModuleRepository extends ListCrudRepository<Module, Long> {

    Module findFirstByModule(String module);
}
