package com.backend.config;

import com.backend.domain.major.Major;
import com.backend.domain.migration.Migration;
import com.backend.domain.migration.MigrationRepository;
import com.backend.domain.module.Module;
import com.backend.domain.role.Roles;
import com.backend.domain.studies.type.StudiesType;
import com.backend.domain.user.Status;
import com.backend.domain.user.User;
import com.backend.domain.user.UserRepository;
import com.backend.service.major.MajorService;
import com.backend.service.module.ModuleService;
import com.backend.service.studies.type.StudiesTypeService;
import com.backend.service.token.TokenService;
import com.backend.service.user.UserReadService;
import com.backend.service.user.UserUpdateService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.backend.controller.utils.SearchUtils.DEFAULT_PASSWORD;
import static com.backend.domain.role.Role.ROLE_ADMIN;
import static com.backend.domain.role.Role.ROLE_USER;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private static final Log LOGGER = LogFactory.getLog(ApplicationStartup.class);

    private final TokenService tokenService;
    private final MajorService majorService;
    private final ModuleService moduleService;
    private final PasswordEncoder passwordEncoder;
    private final UserUpdateService userUpdateService;
    private final StudiesTypeService studiesTypeService;
    private final MigrationRepository migrationRepository;

    @Override
    @Transactional
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        initiallyInsertUsers();
    }


    private void initiallyInsertUsers() {
        Migration migration = migrationRepository.findFirst();
        if(migration == null) {
            migration = new Migration();
        }

        if(!migration.isInitializedUsers()) {
            readExcelFile(migration);
        }

        if(!migration.isCreatedGodUser()) {
            createGodUser(migration);
        }
        tokenService.restoreTokens();
        migrationRepository.save(migration);
    }

    public void readExcelFile(Migration migration) {
        List<User> users = new ArrayList<>();
        Set<StudiesType> studiesTypes = studiesTypeService.findAll();
        Set<Major> majors = majorService.findAll();
        Set<Module> modules = moduleService.findAll();

        try (InputStream file = getClass().getResourceAsStream("/studenti.xlsx");
             Workbook workbook = new XSSFWorkbook(file)) {
             Sheet sheet = workbook.getSheetAt(0); // Assuming you're reading the first sheet

             Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

             while (rowIterator.hasNext()) {
                 Row row = rowIterator.next();
                 Iterator<Cell> cellIterator = row.cellIterator();

                 List<String> rowData = new ArrayList<>();
                 while(cellIterator.hasNext()) {
                     Cell cell = cellIterator.next();
                     if(cell.getCellType() == CellType.NUMERIC) {
                         rowData.add(String.valueOf((long) cell.getNumericCellValue()));
                     }else {
                         rowData.add(cell.toString());
                     }
                 }

                 if(!StringUtils.isEmpty(rowData.get(8))) {
                     User user = new User();
                     user.setIndeks(rowData.get(0));
                     user.setFirstName(rowData.get(1));
                     user.setLastName(rowData.get(2));
                     user.setSignYear(Long.parseLong(rowData.get(6)));
                     user.setStatus(Status.findByDescription(rowData.get(7)));
                     user.setEmail(rowData.get(8));
                     user.setActive(true);
                     user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));

                     StudiesType studiesType = studiesTypes.stream()
                             .filter(type -> Objects.equals(type.getType(), rowData.get(3)))
                             .findFirst()
                             .orElse(null);
                     if(studiesType == null) {
                         studiesType = new StudiesType();
                         studiesType.setType(rowData.get(3));
                         studiesTypes.add(studiesType);
                     }
                     user.setStudiesType(studiesType);

                     Major major = majors.stream()
                             .filter(type -> Objects.equals(type.getMajor(), rowData.get(4)))
                             .findFirst()
                             .orElse(null);
                     if(major == null) {
                         major = new Major();
                         major.setMajor(rowData.get(4));
                         majors.add(major);
                     }
                     user.setMajor(major);

                     if(!StringUtils.isEmpty(rowData.get(5))) {
                         Module module = modules.stream()
                                 .filter(type -> Objects.equals(type.getModule(), rowData.get(5)))
                                 .findFirst()
                                 .orElse(null);
                         if (module == null) {
                             module = new Module();
                             module.setModule(rowData.get(5));
                             modules.add(module);
                         }
                         user.setModule(module);
                     }

                     user.getRoles().add(new Roles(user, ROLE_USER));
                     users.add(user);
                 }
             }
             moduleService.saveAll(modules);
             majorService.saveAll(majors);
             studiesTypeService.saveAll(studiesTypes);
             userUpdateService.saveAll(users);
             migration.setInitializedUsers(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createGodUser(Migration migration) {

        User godUser = new User();
        godUser.setEmail("admin@pmf.kg.ac.rs");
        godUser.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        godUser.setFirstName("Admin");
        godUser.setLastName("Admin");
        godUser.setActive(true);
        godUser.getRoles().add(new Roles(godUser, ROLE_ADMIN));
        godUser.getRoles().add(new Roles(godUser, ROLE_USER));

        userUpdateService.save(godUser);
        migration.setCreatedGodUser(true);

    }

}
