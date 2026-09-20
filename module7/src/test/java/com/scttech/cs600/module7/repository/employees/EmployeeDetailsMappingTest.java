package com.scttech.cs600.module7.repository.employees;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.model.employees.Employee;
import com.scttech.cs600.module7.model.employees.EmployeeStatus;
import com.scttech.cs600.module7.model.employees.EmployeeType;
import com.scttech.cs600.module7.model.employees.faculty.AcademicRank;
import com.scttech.cs600.module7.model.employees.faculty.FacultyDetails;
import com.scttech.cs600.module7.model.employees.faculty.TenureStatus;
import com.scttech.cs600.module7.model.employees.staff.StaffDetails;

import jakarta.persistence.EntityManager;

/**
 * Round-trips {@link Employee}, {@link FacultyDetails} and {@link StaffDetails} through Postgres to
 * check the native enum mappings and the employee-id primary key ({@code @MapsId}).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class EmployeeDetailsMappingTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private EntityManager entityManager;

    private Employee saveEmployee(String number, EmployeeType type) {
        Department department = new Department("CS", "Computer Science");
        entityManager.persist(department);

        Employee employee = new Employee(number, "Ada", "Lovelace", number + "@example.edu",
                department, type, LocalDate.of(2020, 8, 15));
        entityManager.persist(employee);
        return employee;
    }

    @Test
    void employeeEnumsRoundTripAndStatusDefaultsToActive() {
        Employee employee = saveEmployee("E1", EmployeeType.FACULTY);
        entityManager.flush();
        entityManager.clear();

        Employee found = entityManager.find(Employee.class, employee.getId());

        assertThat(found.getEmployeeType()).isEqualTo(EmployeeType.FACULTY);
        assertThat(found.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
    }

    @Test
    void facultyDetailsAreKeyedByTheEmployeeId() {
        Employee employee = saveEmployee("E2", EmployeeType.FACULTY);
        FacultyDetails details = new FacultyDetails(employee, AcademicRank.ASSOCIATE_PROFESSOR);
        details.setTenureStatus(TenureStatus.TENURE_TRACK);
        details.setOfficeLocation("Room 214");
        entityManager.persist(details);
        entityManager.flush();
        entityManager.clear();

        FacultyDetails found = entityManager.find(FacultyDetails.class, employee.getId());

        assertThat(found.getEmployeeId()).isEqualTo(employee.getId());
        assertThat(found.getAcademicRank()).isEqualTo(AcademicRank.ASSOCIATE_PROFESSOR);
        assertThat(found.getTenureStatus()).isEqualTo(TenureStatus.TENURE_TRACK);
        assertThat(found.getOfficeLocation()).isEqualTo("Room 214");
        assertThat(found.getEmployee().getId()).isEqualTo(employee.getId());
    }

    @Test
    void facultyTenureStatusAndOfficeLocationAreOptional() {
        Employee employee = saveEmployee("E3", EmployeeType.FACULTY);
        entityManager.persist(new FacultyDetails(employee, AcademicRank.ADJUNCT));
        entityManager.flush();
        entityManager.clear();

        FacultyDetails found = entityManager.find(FacultyDetails.class, employee.getId());

        assertThat(found.getTenureStatus()).isNull();
        assertThat(found.getOfficeLocation()).isNull();
    }

    @Test
    void staffDetailsAreKeyedByTheEmployeeId() {
        Employee employee = saveEmployee("E4", EmployeeType.STAFF);
        StaffDetails details = new StaffDetails(employee, "Registrar");
        details.setOfficeLocation("Admin Building");
        entityManager.persist(details);
        entityManager.flush();
        entityManager.clear();

        StaffDetails found = entityManager.find(StaffDetails.class, employee.getId());

        assertThat(found.getEmployeeId()).isEqualTo(employee.getId());
        assertThat(found.getJobTitle()).isEqualTo("Registrar");
        assertThat(found.getOfficeLocation()).isEqualTo("Admin Building");
    }

    @Test
    void facultyDetailsForAStaffEmployeeAreRejectedByTheDatabase() {
        Employee staff = saveEmployee("E5", EmployeeType.STAFF);
        entityManager.persist(new FacultyDetails(staff, AcademicRank.PROFESSOR));

        assertThatThrownBy(entityManager::flush)
                .rootCause().hasMessageContaining("faculty_details_employee_fk");
    }

    @Test
    void staffDetailsForAFacultyEmployeeAreRejectedByTheDatabase() {
        Employee faculty = saveEmployee("E6", EmployeeType.FACULTY);
        entityManager.persist(new StaffDetails(faculty, "Registrar"));

        assertThatThrownBy(entityManager::flush)
                .rootCause().hasMessageContaining("staff_details_employee_fk");
    }

    @Test
    void employeeTypeCantChangeWhileADetailsRowExists() {
        Employee employee = saveEmployee("E7", EmployeeType.FACULTY);
        entityManager.persist(new FacultyDetails(employee, AcademicRank.INSTRUCTOR));
        entityManager.flush();

        employee.setEmployeeType(EmployeeType.STAFF);

        assertThatThrownBy(entityManager::flush)
                .rootCause().hasMessageContaining("faculty_details_employee_fk");
    }
}
