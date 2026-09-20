package com.scttech.cs600.module7.service.employees;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scttech.cs600.module7.model.employees.Employee;
import com.scttech.cs600.module7.model.employees.EmployeeType;
import com.scttech.cs600.module7.model.employees.faculty.AcademicRank;
import com.scttech.cs600.module7.model.employees.faculty.FacultyDetails;
import com.scttech.cs600.module7.model.employees.faculty.TenureStatus;
import com.scttech.cs600.module7.model.employees.staff.StaffDetails;
import com.scttech.cs600.module7.repository.employees.EmployeeRepository;
import com.scttech.cs600.module7.repository.employees.faculty.FacultyDetailsRepository;
import com.scttech.cs600.module7.repository.employees.staff.StaffDetailsRepository;
import com.scttech.cs600.module7.service.employees.exception.DuplicateEmployeeException;

/**
 * The employee operations that involve a details table. An employee and its faculty or staff
 * details are separate rows, so each write runs in a single transaction: they are saved (or
 * removed) together or not at all.
 *
 * <p>An employee's type is fixed once it has a details row — the database ties each details table
 * to employees of one type (see docs/module4) and rejects a change.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final FacultyDetailsRepository facultyDetailsRepository;
    private final StaffDetailsRepository staffDetailsRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
            FacultyDetailsRepository facultyDetailsRepository,
            StaffDetailsRepository staffDetailsRepository) {
        this.employeeRepository = employeeRepository;
        this.facultyDetailsRepository = facultyDetailsRepository;
        this.staffDetailsRepository = staffDetailsRepository;
    }

    /**
     * Saves a {@link EmployeeType#FACULTY} employee together with its faculty details, creating
     * the details row if the employee doesn't have one yet.
     *
     * @throws IllegalArgumentException if {@code employee} isn't of type {@code FACULTY}
     * @throws DuplicateEmployeeException if the employee number or email belongs to someone else
     */
    @Transactional
    public Employee saveFaculty(Employee employee, AcademicRank academicRank, TenureStatus tenureStatus,
            String officeLocation) {
        requireType(employee, EmployeeType.FACULTY);
        requireUnique(employee);
        Employee saved = employeeRepository.saveAndFlush(employee);

        FacultyDetails details = facultyDetailsRepository.findById(saved.getId())
                .orElseGet(() -> new FacultyDetails(saved, academicRank));
        details.setAcademicRank(academicRank);
        details.setTenureStatus(tenureStatus);
        details.setOfficeLocation(officeLocation);
        facultyDetailsRepository.save(details);
        return saved;
    }

    /**
     * Saves a {@link EmployeeType#STAFF} employee together with its staff details, creating the
     * details row if the employee doesn't have one yet.
     *
     * @throws IllegalArgumentException if {@code employee} isn't of type {@code STAFF}
     * @throws DuplicateEmployeeException if the employee number or email belongs to someone else
     */
    @Transactional
    public Employee saveStaff(Employee employee, String jobTitle, String officeLocation) {
        requireType(employee, EmployeeType.STAFF);
        requireUnique(employee);
        Employee saved = employeeRepository.saveAndFlush(employee);

        StaffDetails details = staffDetailsRepository.findById(saved.getId())
                .orElseGet(() -> new StaffDetails(saved, jobTitle));
        details.setJobTitle(jobTitle);
        details.setOfficeLocation(officeLocation);
        staffDetailsRepository.save(details);
        return saved;
    }

    /** Deletes the employee along with its faculty or staff details row, whichever it has. */
    @Transactional
    public void delete(Employee employee) {
        facultyDetailsRepository.deleteById(employee.getId());
        staffDetailsRepository.deleteById(employee.getId());
        employeeRepository.delete(employee);
    }

    private void requireType(Employee employee, EmployeeType expected) {
        if (employee.getEmployeeType() != expected) {
            throw new IllegalArgumentException(
                    "Expected a " + expected + " employee but got " + employee.getEmployeeType());
        }
    }

    private void requireUnique(Employee employee) {
        employeeRepository.findByEmployeeNumber(employee.getEmployeeNumber())
                .filter(other -> !other.equals(employee))
                .ifPresent(other -> {
                    throw new DuplicateEmployeeException("employee number", employee.getEmployeeNumber());
                });
        employeeRepository.findByEmail(employee.getEmail())
                .filter(other -> !other.equals(employee))
                .ifPresent(other -> {
                    throw new DuplicateEmployeeException("email", employee.getEmail());
                });
    }
}
