package com.scttech.cs600.module7.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.scttech.cs600.module7.model.department.Department;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class DepartmentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private DepartmentRepository departmentRepository;

    Department createStandardCsDepartment() {
        Department createdDepartment = departmentRepository.save(new Department("CS", "Computer Science", "CS Department"));

        assertThat(createdDepartment.getId()).isNotNull();
        
        return createdDepartment;
    }

    @Test
    void createsAndReadsADepartment() {
        Department saved = departmentRepository.save(new Department("CS", "Computer Science"));

        assertThat(saved.getId()).isNotNull();

        Optional<Department> found = departmentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("CS");
        assertThat(found.get().getName()).isEqualTo("Computer Science");
    }

    @Test
    void createsAndReadsADepartmentWithDescription() {
        Department saved = createStandardCsDepartment();

        Optional<Department> found = departmentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("CS");
        assertThat(found.get().getName()).isEqualTo("Computer Science");
        assertThat(found.get().getDescription()).isEqualTo("CS Department");
    }

    @Test
    void findsADepartmentByCode() {
        createStandardCsDepartment();

        Optional<Department> found = departmentRepository.findByCode("CS");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Computer Science");
    }

    @Test
    void updatesADepartment() {
        Department saved = createStandardCsDepartment();

        saved.setName("Computer Sciences");
        departmentRepository.save(saved);

        Department updated = departmentRepository.findById(saved.getId()).orElseThrow();

        assertThat(updated.getName()).isEqualTo("Computer Sciences");
    }

    @Test
    void deletesADepartment() {
        Department saved = createStandardCsDepartment();

        departmentRepository.deleteById(saved.getId());

        assertThat(departmentRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void listsAllDepartments() {
        departmentRepository.save(new Department("CS", "Computer Science"));
        departmentRepository.save(new Department("MATH", "Mathematics"));

        List<Department> all = departmentRepository.findAll();

        assertThat(all).hasSize(2);
    }

}
