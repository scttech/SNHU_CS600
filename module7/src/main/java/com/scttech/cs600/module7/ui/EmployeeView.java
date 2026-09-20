package com.scttech.cs600.module7.ui;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.model.employees.Employee;
import com.scttech.cs600.module7.model.employees.EmployeeStatus;
import com.scttech.cs600.module7.model.employees.EmployeeType;
import com.scttech.cs600.module7.model.employees.faculty.AcademicRank;
import com.scttech.cs600.module7.model.employees.faculty.FacultyDetails;
import com.scttech.cs600.module7.model.employees.faculty.TenureStatus;
import com.scttech.cs600.module7.model.employees.staff.StaffDetails;
import com.scttech.cs600.module7.repository.department.DepartmentRepository;
import com.scttech.cs600.module7.repository.employees.EmployeeRepository;
import com.scttech.cs600.module7.repository.employees.faculty.FacultyDetailsRepository;
import com.scttech.cs600.module7.repository.employees.staff.StaffDetailsRepository;
import com.scttech.cs600.module7.service.employees.EmployeeService;
import com.scttech.cs600.module7.service.employees.exception.DuplicateEmployeeException;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;

import jakarta.annotation.security.PermitAll;

/**
 * Single-screen CRUD for faculty and staff, the two kinds of {@code employees} row (see
 * docs/module4). The form shows the shared employee fields plus the ones that belong to the
 * employee's type: rank and tenure for faculty, job title for staff, and an office for both.
 *
 * <p>The type is chosen by which "Add" button starts the form and can't be changed afterwards — the
 * database ties each details table to employees of one type. The shared fields are bound to the
 * {@link Employee} itself, so its bean validation annotations drive the form the same way as in
 * {@link CourseView}. The type-specific fields aren't {@link Employee} properties (they live in
 * {@link FacultyDetails} and {@link StaffDetails}), so they're bound through a {@link Details}
 * draft and written back, together with the employee, by {@link EmployeeService}.
 *
 * <p>Reached from {@link DashboardView}'s "Faculty &amp; Staff" tile.
 */
@Route("employees")
@PageTitle("Faculty & Staff")
@PermitAll
public class EmployeeView extends VerticalLayout {

    /** Working copy of the type-specific fields for the employee being edited. */
    private static class Details {
        AcademicRank academicRank;
        TenureStatus tenureStatus;
        String jobTitle = "";
        String officeLocation = "";
    }

    private final EmployeeRepository employeeRepository;
    private final FacultyDetailsRepository facultyDetailsRepository;
    private final StaffDetailsRepository staffDetailsRepository;
    private final EmployeeService employeeService;

    /** Details rows by employee id, for the grid's role column and for loading the form. */
    private Map<UUID, FacultyDetails> facultyByEmployee = Map.of();
    private Map<UUID, StaffDetails> staffByEmployee = Map.of();
    private Details details = new Details();

    private final Grid<Employee> grid = new Grid<>(Employee.class, false);
    private final TextField type = new TextField("Type");
    private final TextField employeeNumber = new TextField("Employee number");
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final TextField email = new TextField("Email");
    private final ComboBox<Department> department = new ComboBox<>("Department");
    private final DatePicker hireDate = new DatePicker("Hire date");
    private final ComboBox<EmployeeStatus> status = new ComboBox<>("Status");
    private final ComboBox<AcademicRank> academicRank = new ComboBox<>("Academic rank");
    private final ComboBox<TenureStatus> tenureStatus = new ComboBox<>("Tenure status");
    private final TextField jobTitle = new TextField("Job title");
    private final TextField officeLocation = new TextField("Office location");
    private final Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button addFaculty = new Button("Add faculty");
    private final Button addStaff = new Button("Add staff");

    @SuppressWarnings("null")
    public EmployeeView(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository,
            FacultyDetailsRepository facultyDetailsRepository, StaffDetailsRepository staffDetailsRepository,
            EmployeeService employeeService, AuthenticationContext authenticationContext) {
        this.employeeRepository = employeeRepository;
        this.facultyDetailsRepository = facultyDetailsRepository;
        this.staffDetailsRepository = staffDetailsRepository;
        this.employeeService = employeeService;
        setSizeFull();

        grid.addColumn(Employee::getEmployeeNumber).setHeader("Employee number").setAutoWidth(true);
        grid.addColumn(employee -> employee.getLastName() + ", " + employee.getFirstName())
                .setHeader("Name").setAutoWidth(true);
        grid.addColumn(employee -> label(employee.getEmployeeType())).setHeader("Type").setAutoWidth(true);
        grid.addColumn(employee -> employee.getDepartment() == null ? "" : employee.getDepartment().getCode())
                .setHeader("Department").setAutoWidth(true);
        grid.addColumn(this::role).setHeader("Rank / job title").setAutoWidth(true);
        grid.addColumn(employee -> label(employee.getStatus())).setHeader("Status").setAutoWidth(true);
        grid.addColumn(Employee::getHireDate).setHeader("Hired").setAutoWidth(true);
        grid.addColumn(Employee::getEmail).setHeader("Email").setAutoWidth(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> editEmployee(event.getValue()));

        type.setReadOnly(true);
        department.setItems(departmentRepository.findAll(Sort.by("code")));
        department.setItemLabelGenerator(d -> d.getCode() + " – " + d.getName());
        status.setItems(EmployeeStatus.values());
        status.setItemLabelGenerator(EmployeeView::label);
        academicRank.setItems(AcademicRank.values());
        academicRank.setItemLabelGenerator(EmployeeView::label);
        academicRank.setRequiredIndicatorVisible(true);
        tenureStatus.setItems(TenureStatus.values());
        tenureStatus.setItemLabelGenerator(EmployeeView::label);
        tenureStatus.setClearButtonVisible(true);
        jobTitle.setRequiredIndicatorVisible(true);

        // Bound by property name so BeanValidationBinder applies the annotations on Employee.
        binder.forField(employeeNumber).bind("employeeNumber");
        binder.forField(firstName).bind("firstName");
        binder.forField(lastName).bind("lastName");
        binder.forField(email).bind("email");
        binder.forField(department).asRequired("Department is required").bind("department");
        binder.forField(hireDate).asRequired("Hire date is required").bind("hireDate");
        binder.forField(status).asRequired("Status is required").bind("status");

        // The type-specific fields only apply to one type each, so their checks only run for it.
        binder.forField(academicRank)
                .withValidator(rank -> !isFaculty() || rank != null, "Academic rank is required")
                .bind(employee -> details.academicRank, (employee, rank) -> details.academicRank = rank);
        binder.forField(tenureStatus)
                .bind(employee -> details.tenureStatus, (employee, tenure) -> details.tenureStatus = tenure);
        binder.forField(jobTitle)
                .withValidator(title -> isFaculty() || !title.isBlank(), "Job title is required")
                .withValidator(new StringLengthValidator("Job title can be at most 100 characters", null, 100))
                .bind(employee -> details.jobTitle, (employee, title) -> details.jobTitle = title);
        binder.forField(officeLocation)
                .withValidator(new StringLengthValidator("Office location can be at most 100 characters", null, 100))
                .bind(employee -> details.officeLocation, (employee, office) -> details.officeLocation = office);

        save.addClickListener(event -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> delete());
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(event -> editEmployee(null));
        addFaculty.addClickListener(event -> addNew(EmployeeType.FACULTY));
        addStaff.addClickListener(event -> addNew(EmployeeType.STAFF));

        VerticalLayout form = new VerticalLayout(type, employeeNumber, firstName, lastName, email, department,
                hireDate, status, academicRank, tenureStatus, jobTitle, officeLocation,
                new HorizontalLayout(save, delete, cancel));
        form.setWidth("22em");

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);

        add(new AppHeader("Faculty & Staff", authenticationContext),
                new RouterLink("← Dashboard", DashboardView.class),
                new HorizontalLayout(addFaculty, addStaff), content);

        refreshGrid();
        editEmployee(null);
    }

    private void addNew(EmployeeType employeeType) {
        grid.asSingleSelect().clear();
        editEmployee(new Employee("", "", "", "", null, employeeType, null));
    }

    private void editEmployee(Employee employee) {
        details = detailsOf(employee);
        binder.setBean(employee);
        setFormEnabled(employee != null);
        type.setValue(employee == null ? "" : label(employee.getEmployeeType()));

        boolean faculty = employee != null && employee.getEmployeeType() == EmployeeType.FACULTY;
        boolean staff = employee != null && employee.getEmployeeType() == EmployeeType.STAFF;
        academicRank.setVisible(faculty);
        tenureStatus.setVisible(faculty);
        jobTitle.setVisible(staff);
    }

    /** The form's working copy of the type-specific fields, filled from the saved details if any. */
    private Details detailsOf(Employee employee) {
        Details draft = new Details();
        if (employee == null || employee.getId() == null) {
            return draft;
        }
        FacultyDetails faculty = facultyByEmployee.get(employee.getId());
        if (faculty != null) {
            draft.academicRank = faculty.getAcademicRank();
            draft.tenureStatus = faculty.getTenureStatus();
            draft.officeLocation = orEmpty(faculty.getOfficeLocation());
        }
        StaffDetails staff = staffByEmployee.get(employee.getId());
        if (staff != null) {
            draft.jobTitle = orEmpty(staff.getJobTitle());
            draft.officeLocation = orEmpty(staff.getOfficeLocation());
        }
        return draft;
    }

    private void setFormEnabled(boolean enabled) {
        List<HasValueAndElement<?, ?>> fields = List.of(employeeNumber, firstName, lastName, email, department,
                hireDate, status, academicRank, tenureStatus, jobTitle, officeLocation);
        fields.forEach(field -> field.setEnabled(enabled));
        save.setEnabled(enabled);
        cancel.setEnabled(enabled);
        delete.setEnabled(enabled && binder.getBean().getId() != null);
    }

    private boolean isFaculty() {
        return binder.getBean() != null && binder.getBean().getEmployeeType() == EmployeeType.FACULTY;
    }

    private void save() {
        if (!binder.isValid()) {
            binder.validate();
            return;
        }
        Employee employee = binder.getBean();
        try {
            if (employee.getEmployeeType() == EmployeeType.FACULTY) {
                employeeService.saveFaculty(employee, details.academicRank, details.tenureStatus,
                        blankToNull(details.officeLocation));
            } else {
                employeeService.saveStaff(employee, details.jobTitle.strip(),
                        blankToNull(details.officeLocation));
            }
        } catch (DuplicateEmployeeException e) {
            // Keep the form open so the employee number or email can be corrected.
            Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        Notification.show(label(employee.getEmployeeType()) + " member saved")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        grid.asSingleSelect().clear();
        editEmployee(null);
        refreshGrid();
    }

    private void delete() {
        Employee employee = binder.getBean();
        if (employee == null || employee.getId() == null) {
            return;
        }
        employeeService.delete(employee);
        Notification.show("Employee deleted").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        grid.asSingleSelect().clear();
        editEmployee(null);
        refreshGrid();
    }

    @SuppressWarnings("null")
    private void refreshGrid() {
        facultyByEmployee = facultyDetailsRepository.findAll().stream()
                .collect(Collectors.toMap(FacultyDetails::getEmployeeId, Function.identity()));
        staffByEmployee = staffDetailsRepository.findAll().stream()
                .collect(Collectors.toMap(StaffDetails::getEmployeeId, Function.identity()));
        grid.setItems(employeeRepository.findAll().stream()
                .sorted(Comparator.comparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Employee::getFirstName, String.CASE_INSENSITIVE_ORDER))
                .toList());
    }

    /** Academic rank for faculty, job title for staff — whichever the employee's details hold. */
    private String role(Employee employee) {
        FacultyDetails faculty = facultyByEmployee.get(employee.getId());
        if (faculty != null) {
            return label(faculty.getAcademicRank());
        }
        StaffDetails staff = staffByEmployee.get(employee.getId());
        return staff == null ? "" : staff.getJobTitle();
    }

    /** {@code ASSISTANT_PROFESSOR} as "Assistant Professor". */
    private static String label(Enum<?> value) {
        if (value == null) {
            return "";
        }
        return Arrays.stream(value.name().split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
