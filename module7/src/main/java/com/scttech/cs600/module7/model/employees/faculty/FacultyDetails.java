package com.scttech.cs600.module7.model.employees.faculty;

import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.scttech.cs600.module7.model.employees.Employee;
import com.scttech.cs600.module7.model.employees.EmployeeType;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Faculty-only attributes, one row per {@link Employee} whose type is
 * {@link EmployeeType#FACULTY}, per docs/module4/README.md. The primary key is the employee's own
 * id ({@code @MapsId}), so a details row can't exist without its employee.
 *
 * <p>The database also enforces the "where type is FACULTY" part: {@code employee_type} is pinned
 * to {@code FACULTY} by a {@code CHECK}, and the foreign key is the composite
 * {@code (employee_id, employee_type)} into {@code employees}, so a row can't point at a staff
 * employee, and an employee with a faculty row can't be changed to another type.
 */
@Entity
@Table(name = "faculty_details", check = @CheckConstraint(
        name = "faculty_details_employee_type_check",
        constraint = "employee_type = 'FACULTY'"))
public class FacultyDetails {

    @Id
    @Column(name = "employee_id")
    private UUID employeeId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(
            name = "faculty_details_employee_fk",
            foreignKeyDefinition = "FOREIGN KEY (employee_id, employee_type) "
                    + "REFERENCES employees (id, employee_type)"))
    private Employee employee;

    // Exists only so the composite foreign key above has its second column. It's never written by
    // JPA (the column default supplies FACULTY) and there's nothing to read from it.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @ColumnDefault("'FACULTY'")
    @Column(name = "employee_type", nullable = false, insertable = false, updatable = false)
    @SuppressWarnings("unused")
    private EmployeeType employeeType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "academic_rank", nullable = false)
    private AcademicRank academicRank;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tenure_status")
    private TenureStatus tenureStatus;

    @Size(max = 100)
    @Column(name = "office_location", length = 100)
    private String officeLocation;

    protected FacultyDetails() {
        // required by JPA
    }

    public FacultyDetails(Employee employee, AcademicRank academicRank) {
        this.employee = employee;
        this.academicRank = academicRank;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public AcademicRank getAcademicRank() {
        return academicRank;
    }

    public void setAcademicRank(AcademicRank academicRank) {
        this.academicRank = academicRank;
    }

    public TenureStatus getTenureStatus() {
        return tenureStatus;
    }

    public void setTenureStatus(TenureStatus tenureStatus) {
        this.tenureStatus = tenureStatus;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    /**
     * Two details rows are the same when they share the employee id they're keyed by. A row whose
     * employee hasn't been saved yet has no id and is only equal to itself.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof FacultyDetails other && employeeId != null
                && employeeId.equals(other.getEmployeeId());
    }

    /**
     * Constant on purpose: {@code employeeId} is null until the row is persisted, and a hash that
     * changed on save would break hash-based collections holding an unsaved row.
     */
    @Override
    public int hashCode() {
        return FacultyDetails.class.hashCode();
    }
}
