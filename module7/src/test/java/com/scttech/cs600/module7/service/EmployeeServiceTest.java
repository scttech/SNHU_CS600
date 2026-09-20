package com.scttech.cs600.module7.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.model.employees.Employee;
import com.scttech.cs600.module7.model.employees.EmployeeType;
import com.scttech.cs600.module7.model.employees.faculty.AcademicRank;
import com.scttech.cs600.module7.model.employees.faculty.FacultyDetails;
import com.scttech.cs600.module7.model.employees.faculty.TenureStatus;
import com.scttech.cs600.module7.model.employees.staff.StaffDetails;
import com.scttech.cs600.module7.repository.employees.EmployeeRepository;
import com.scttech.cs600.module7.repository.employees.faculty.FacultyDetailsRepository;
import com.scttech.cs600.module7.repository.employees.staff.StaffDetailsRepository;
import com.scttech.cs600.module7.service.employees.EmployeeService;
import com.scttech.cs600.module7.service.employees.exception.DuplicateEmployeeException;

import jakarta.persistence.EntityManager;

/**
 * Runs {@link EmployeeService} against a real, throwaway Postgres container. The persistence
 * context is flushed and cleared before each service call so the employees passed in are detached,
 * as they are when they come out of the Vaadin grid.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(EmployeeService.class)
class EmployeeServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FacultyDetailsRepository facultyDetailsRepository;

    @Autowired
    private StaffDetailsRepository staffDetailsRepository;

    @Autowired
    private EntityManager entityManager;

    private Department department() {
        Department department = new Department("CS", "Computer Science");
        entityManager.persist(department);
        return department;
    }

    private Employee newEmployee(String number, EmployeeType type, Department department) {
        return new Employee(number, "Ada", "Lovelace", number + "@example.edu", department, type,
                LocalDate.of(2020, 8, 15));
    }

    private void detach() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void savesANewFacultyMemberWithTheirDetails() {
        Department department = department();
        detach();

        Employee saved = employeeService.saveFaculty(newEmployee("F1", EmployeeType.FACULTY, department),
                AcademicRank.PROFESSOR, TenureStatus.TENURED, "Room 214");

        assertThat(saved.getId()).isNotNull();
        FacultyDetails details = facultyDetailsRepository.findById(saved.getId()).orElseThrow();
        assertThat(details.getAcademicRank()).isEqualTo(AcademicRank.PROFESSOR);
        assertThat(details.getTenureStatus()).isEqualTo(TenureStatus.TENURED);
        assertThat(details.getOfficeLocation()).isEqualTo("Room 214");
    }

    @Test
    void updatingAFacultyMemberChangesTheExistingDetailsRow() {
        Department department = department();
        detach();
        Employee saved = employeeService.saveFaculty(newEmployee("F2", EmployeeType.FACULTY, department),
                AcademicRank.ASSISTANT_PROFESSOR, TenureStatus.TENURE_TRACK, "Room 1");
        detach();

        Employee edited = employeeRepository.findAll().get(0);
        edited.setLastName("Byron");
        employeeService.saveFaculty(edited, AcademicRank.ASSOCIATE_PROFESSOR, null, null);
        detach();

        assertThat(employeeRepository.findById(saved.getId()).orElseThrow().getLastName()).isEqualTo("Byron");
        assertThat(facultyDetailsRepository.count()).isEqualTo(1);
        FacultyDetails details = facultyDetailsRepository.findById(saved.getId()).orElseThrow();
        assertThat(details.getAcademicRank()).isEqualTo(AcademicRank.ASSOCIATE_PROFESSOR);
        assertThat(details.getTenureStatus()).isNull();
        assertThat(details.getOfficeLocation()).isNull();
    }

    @Test
    void savesANewStaffMemberWithTheirDetails() {
        Department department = department();
        detach();

        Employee saved = employeeService.saveStaff(newEmployee("S1", EmployeeType.STAFF, department),
                "Registrar", "Admin Building");

        StaffDetails details = staffDetailsRepository.findById(saved.getId()).orElseThrow();
        assertThat(details.getJobTitle()).isEqualTo("Registrar");
        assertThat(details.getOfficeLocation()).isEqualTo("Admin Building");
    }

    @Test
    void refusesToSaveAStaffEmployeeAsFaculty() {
        Department department = department();
        detach();
        Employee staff = newEmployee("S2", EmployeeType.STAFF, department);

        assertThatThrownBy(() -> employeeService.saveFaculty(staff, AcademicRank.PROFESSOR, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(employeeRepository.count()).isZero();
    }

    @Test
    void rejectsAnEmployeeNumberThatBelongsToSomeoneElse() {
        Department department = department();
        detach();
        employeeService.saveStaff(newEmployee("S3", EmployeeType.STAFF, department), "Clerk", null);
        detach();

        Employee duplicate = new Employee("S3", "Other", "Person", "other@example.edu", department,
                EmployeeType.STAFF, LocalDate.of(2021, 1, 4));

        assertThatThrownBy(() -> employeeService.saveStaff(duplicate, "Clerk", null))
                .isInstanceOf(DuplicateEmployeeException.class)
                .hasMessageContaining("employee number");
        assertThat(employeeRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectsAnEmailThatBelongsToSomeoneElse() {
        Department department = department();
        detach();
        employeeService.saveStaff(newEmployee("S4", EmployeeType.STAFF, department), "Clerk", null);
        detach();

        Employee duplicate = new Employee("S5", "Other", "Person", "S4@example.edu", department,
                EmployeeType.STAFF, LocalDate.of(2021, 1, 4));

        assertThatThrownBy(() -> employeeService.saveStaff(duplicate, "Clerk", null))
                .isInstanceOf(DuplicateEmployeeException.class)
                .hasMessageContaining("email");
    }

    @Test
    void resavingAnEmployeeIsNotADuplicateOfThemselves() {
        Department department = department();
        detach();
        employeeService.saveStaff(newEmployee("S6", EmployeeType.STAFF, department), "Clerk", null);
        detach();

        Employee edited = employeeRepository.findAll().get(0);

        employeeService.saveStaff(edited, "Senior Clerk", null);
        detach();

        assertThat(staffDetailsRepository.findById(edited.getId()).orElseThrow().getJobTitle())
                .isEqualTo("Senior Clerk");
    }

    @Test
    void deletingAFacultyMemberRemovesTheirDetailsToo() {
        Department department = department();
        detach();
        Employee saved = employeeService.saveFaculty(newEmployee("F3", EmployeeType.FACULTY, department),
                AcademicRank.ADJUNCT, null, null);
        detach();

        employeeService.delete(employeeRepository.findById(saved.getId()).orElseThrow());
        detach();

        assertThat(employeeRepository.count()).isZero();
        assertThat(facultyDetailsRepository.count()).isZero();
    }

    @Test
    void deletingAStaffMemberRemovesTheirDetailsToo() {
        Department department = department();
        detach();
        Employee saved = employeeService.saveStaff(newEmployee("S7", EmployeeType.STAFF, department),
                "Clerk", null);
        detach();

        employeeService.delete(employeeRepository.findById(saved.getId()).orElseThrow());
        detach();

        assertThat(employeeRepository.count()).isZero();
        assertThat(staffDetailsRepository.count()).isZero();
    }
}
