package com.scttech.cs600.module7.model.employees.staff;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Staff-only attributes, one row per {@link Employee} whose type is {@link EmployeeType#STAFF},
 * per docs/module4/README.md. The primary key is the employee's own id ({@code @MapsId}), so a
 * details row can't exist without its employee.
 *
 * <p>The database also enforces the "where type is STAFF" part: {@code employee_type} is pinned to
 * {@code STAFF} by a {@code CHECK}, and the foreign key is the composite
 * {@code (employee_id, employee_type)} into {@code employees}, so a row can't point at a faculty
 * employee, and an employee with a staff row can't be changed to another type.
 */
@Entity
@Table(name = "staff_details", check = @CheckConstraint(
        name = "staff_details_employee_type_check",
        constraint = "employee_type = 'STAFF'"))
public class StaffDetails {

    @Id
    @Column(name = "employee_id")
    private UUID employeeId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(
            name = "staff_details_employee_fk",
            foreignKeyDefinition = "FOREIGN KEY (employee_id, employee_type) "
                    + "REFERENCES employees (id, employee_type)"))
    private Employee employee;

    // Exists only so the composite foreign key above has its second column. It's never written by
    // JPA (the column default supplies STAFF) and there's nothing to read from it.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @ColumnDefault("'STAFF'")
    @Column(name = "employee_type", nullable = false, insertable = false, updatable = false)
    @SuppressWarnings("unused")
    private EmployeeType employeeType;

    @NotBlank
    @Size(max = 100)
    @Column(name = "job_title", nullable = false, length = 100)
    private String jobTitle;

    @Size(max = 100)
    @Column(name = "office_location", length = 100)
    private String officeLocation;

    protected StaffDetails() {
        // required by JPA
    }

    public StaffDetails(Employee employee, String jobTitle) {
        this.employee = employee;
        this.jobTitle = jobTitle;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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
        return o instanceof StaffDetails other && employeeId != null
                && employeeId.equals(other.getEmployeeId());
    }

    /**
     * Constant on purpose: {@code employeeId} is null until the row is persisted, and a hash that
     * changed on save would break hash-based collections holding an unsaved row.
     */
    @Override
    public int hashCode() {
        return StaffDetails.class.hashCode();
    }
}
