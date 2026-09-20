package com.scttech.cs600.module7.ui;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.model.students.Student;
import com.scttech.cs600.module7.model.students.StudentStatus;
import com.scttech.cs600.module7.repository.department.DepartmentRepository;
import com.scttech.cs600.module7.repository.students.StudentRepository;
import com.scttech.cs600.module7.service.students.StudentService;
import com.scttech.cs600.module7.service.students.exception.DuplicateStudentException;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;

import jakarta.annotation.security.PermitAll;

/**
 * Single-screen CRUD for the {@code students} table (see docs/module4). Every field is a
 * {@link Student} property, so the form is one {@link BeanValidationBinder} bound by property name,
 * which is what makes the bean validation annotations on {@link Student} drive the form — binding
 * with method references would skip them.
 *
 * <p>The department is optional: it's the student's declared major, and a student may not have
 * declared one yet.
 *
 * <p>Reached from {@link DashboardView}'s "Students" tile.
 */
@Route("students")
@PageTitle("Students")
@PermitAll
public class StudentView extends VerticalLayout {

    private final StudentRepository studentRepository;
    private final StudentService studentService;

    private final Grid<Student> grid = new Grid<>(Student.class, false);
    private final TextField studentNumber = new TextField("Student number");
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final TextField email = new TextField("Email");
    private final DatePicker dateOfBirth = new DatePicker("Date of birth");
    private final DatePicker enrollmentDate = new DatePicker("Enrollment date");
    private final ComboBox<Department> department = new ComboBox<>("Major (department)");
    private final ComboBox<StudentStatus> status = new ComboBox<>("Status");
    private final Binder<Student> binder = new BeanValidationBinder<>(Student.class);

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button addNew = new Button("Add student");

    @SuppressWarnings("null")
    public StudentView(StudentRepository studentRepository, DepartmentRepository departmentRepository,
            StudentService studentService, AuthenticationContext authenticationContext) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
        setSizeFull();

        grid.addColumn(Student::getStudentNumber).setHeader("Student number").setAutoWidth(true);
        grid.addColumn(student -> student.getLastName() + ", " + student.getFirstName())
                .setHeader("Name").setAutoWidth(true);
        grid.addColumn(student -> student.getDepartment() == null ? "Undeclared" : student.getDepartment().getCode())
                .setHeader("Major").setAutoWidth(true);
        grid.addColumn(student -> label(student.getStatus())).setHeader("Status").setAutoWidth(true);
        grid.addColumn(Student::getEnrollmentDate).setHeader("Enrolled").setAutoWidth(true);
        grid.addColumn(Student::getEmail).setHeader("Email").setAutoWidth(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> editStudent(event.getValue()));

        department.setItems(departmentRepository.findAll(Sort.by("code")));
        department.setItemLabelGenerator(d -> d.getCode() + " – " + d.getName());
        department.setClearButtonVisible(true);
        department.setPlaceholder("Undeclared");
        status.setItems(StudentStatus.values());
        status.setItemLabelGenerator(StudentView::label);

        // Bound by property name so BeanValidationBinder applies the annotations on Student.
        binder.forField(studentNumber).bind("studentNumber");
        binder.forField(firstName).bind("firstName");
        binder.forField(lastName).bind("lastName");
        binder.forField(email).bind("email");
        binder.forField(dateOfBirth).bind("dateOfBirth");
        binder.forField(enrollmentDate).asRequired("Enrollment date is required").bind("enrollmentDate");
        binder.forField(department).bind("department");
        binder.forField(status).asRequired("Status is required").bind("status");

        save.addClickListener(event -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> delete());
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(event -> editStudent(null));
        addNew.addClickListener(event -> {
            grid.asSingleSelect().clear();
            editStudent(new Student("", "", "", "", LocalDate.now(), null));
        });

        VerticalLayout form = new VerticalLayout(studentNumber, firstName, lastName, email, dateOfBirth,
                enrollmentDate, department, status, new HorizontalLayout(save, delete, cancel));
        form.setWidth("22em");

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);

        add(new AppHeader("Students", authenticationContext), new RouterLink("← Dashboard", DashboardView.class),
                addNew, content);

        refreshGrid();
        editStudent(null);
    }

    private void editStudent(Student student) {
        binder.setBean(student);
        setFormEnabled(student != null);
    }

    private void setFormEnabled(boolean enabled) {
        studentNumber.setEnabled(enabled);
        firstName.setEnabled(enabled);
        lastName.setEnabled(enabled);
        email.setEnabled(enabled);
        dateOfBirth.setEnabled(enabled);
        enrollmentDate.setEnabled(enabled);
        department.setEnabled(enabled);
        status.setEnabled(enabled);
        save.setEnabled(enabled);
        cancel.setEnabled(enabled);
        delete.setEnabled(enabled && binder.getBean().getId() != null);
    }

    private void save() {
        if (!binder.isValid()) {
            binder.validate();
            return;
        }
        try {
            studentService.save(binder.getBean());
        } catch (DuplicateStudentException e) {
            // Keep the form open so the student number or email can be corrected.
            Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        Notification.show("Student saved").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        grid.asSingleSelect().clear();
        editStudent(null);
        refreshGrid();
    }

    private void delete() {
        Student student = binder.getBean();
        if (student == null || student.getId() == null) {
            return;
        }
        studentService.delete(student);
        Notification.show("Student deleted").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        grid.asSingleSelect().clear();
        editStudent(null);
        refreshGrid();
    }

    @SuppressWarnings("null")
    private void refreshGrid() {
        grid.setItems(studentRepository.findAll().stream()
                .sorted(Comparator.comparing(Student::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Student::getFirstName, String.CASE_INSENSITIVE_ORDER))
                .toList());
    }

    /** {@code GRADUATED} as "Graduated". */
    private static String label(Enum<?> value) {
        if (value == null) {
            return "";
        }
        return Arrays.stream(value.name().split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
