package com.scttech.cs600.module7.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.scttech.cs600.module7.model.Course;
import com.scttech.cs600.module7.model.CoursePrerequisite;
import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.repository.CoursePrerequisiteRepository;
import com.scttech.cs600.module7.repository.CourseRepository;
import com.scttech.cs600.module7.repository.DepartmentRepository;
import com.scttech.cs600.module7.service.CourseService;
import com.scttech.cs600.module7.service.PrerequisiteCycleException;

import org.springframework.data.domain.Sort;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;

import jakarta.annotation.security.PermitAll;

/**
 * Single-screen CRUD for the {@code courses} table only — later modules grow this into a
 * multi-view UI covering the rest of the registrar schema (see docs/module4). The bean validation
 * annotations on {@link Course} (e.g. {@code @NotBlank}, {@code @Min}) drive the form's validation
 * via {@link BeanValidationBinder}, so the rules live in one place instead of being duplicated here.
 *
 * <p>A course's prerequisites aren't a {@link Course} property, so they sit outside the binder:
 * the picker is loaded from {@link CoursePrerequisiteRepository} on selection and written back,
 * together with the course, by {@link CourseService}.
 *
 * <p>{@code @PermitAll} is required, not decorative: once {@link LoginView} switches this app over
 * to Vaadin's Spring Security integration, every view needs an access annotation
 * ({@code @PermitAll}, {@code @RolesAllowed}, or {@link AnonymousAllowed}) or navigation to it is
 * denied outright, authenticated or not.
 *
 * <p>Reached from {@link DashboardView}'s "Course Catalog" tile — {@code ""} belongs to the
 * Dashboard now (see docs/module7/README.md).
 */
@Route("courses")
@PageTitle("Courses")
@PermitAll
public class CourseView extends VerticalLayout {

    private final CourseRepository courseRepository;
    private final CoursePrerequisiteRepository coursePrerequisiteRepository;
    private final CourseService courseService;

    /** Every course sorted by code: the prerequisite picker's choices (minus the course being edited). */
    private List<Course> allCourses = List.of();
    /** Course id to its prerequisites' codes, e.g. {@code "CS-300, CS-350"}, for the grid column. */
    private Map<UUID, String> prerequisiteCodes = Map.of();

    private final Grid<Course> grid = new Grid<>(Course.class, false);
    private final TextField courseCode = new TextField("Course code");
    private final TextField title = new TextField("Title");
    private final IntegerField credits = new IntegerField("Credits");
    private final ComboBox<Department> department = new ComboBox<>("Department");
    private final MultiSelectComboBox<Course> prerequisites = new MultiSelectComboBox<>("Prerequisites");
    private final Binder<Course> binder = new BeanValidationBinder<>(Course.class);

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button addNew = new Button("Add course");

    @SuppressWarnings("null")
    public CourseView(CourseRepository courseRepository, DepartmentRepository departmentRepository,
            CoursePrerequisiteRepository coursePrerequisiteRepository, CourseService courseService,
            AuthenticationContext authenticationContext) {
        this.courseRepository = courseRepository;
        this.coursePrerequisiteRepository = coursePrerequisiteRepository;
        this.courseService = courseService;
        setSizeFull();

        grid.addColumn(Course::getCourseCode).setHeader("Course code").setAutoWidth(true);
        grid.addColumn(Course::getTitle).setHeader("Title").setAutoWidth(true);
        grid.addColumn(Course::getCredits).setHeader("Credits").setAutoWidth(true);
        grid.addColumn(course -> course.getDepartment() == null ? "" : course.getDepartment().getCode())
                .setHeader("Department").setAutoWidth(true);
        grid.addColumn(course -> prerequisiteCodes.getOrDefault(course.getId(), ""))
                .setHeader("Prerequisites").setAutoWidth(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> editCourse(event.getValue()));

        department.setItems(departmentRepository.findAll(Sort.by("code")));
        department.setItemLabelGenerator(d -> d.getCode() + " – " + d.getName());
        prerequisites.setItemLabelGenerator(Course::getCourseCode);

        binder.forField(courseCode).bind(Course::getCourseCode, Course::setCourseCode);
        binder.forField(title).bind(Course::getTitle, Course::setTitle);
        binder.forField(credits).bind(Course::getCredits, Course::setCredits);
        binder.forField(department).asRequired("Department is required")
                .bind(Course::getDepartment, Course::setDepartment);

        save.addClickListener(event -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> delete());
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(event -> editCourse(null));
        addNew.addClickListener(event -> {
            grid.asSingleSelect().clear();
            editCourse(new Course("", "", 1));
        });

        VerticalLayout form = new VerticalLayout(courseCode, title, credits, department, prerequisites,
                new HorizontalLayout(save, delete, cancel));
        form.setWidth("20em");

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);

        add(new AppHeader("Courses", authenticationContext), new RouterLink("← Dashboard", DashboardView.class), addNew, content);

        refreshGrid();
        editCourse(null);
    }

    private void editCourse(Course course) {
        binder.setBean(course);
        setFormEnabled(course != null);
        loadPrerequisites(course);
    }

    private void loadPrerequisites(Course course) {
        prerequisites.clear();
        if (course == null) {
            prerequisites.setItems(List.of());
            return;
        }
        // Not offered: the course itself and every course that already requires it, since either
        // would make a prerequisite cycle. Hiding them is a convenience; CourseService.save is what
        // actually rejects a cycle.
        Set<Course> dependents = courseService.dependentsOf(course);
        prerequisites.setItems(allCourses.stream()
                .filter(c -> !c.equals(course) && !dependents.contains(c))
                .toList());
        if (course.getId() != null) {
            prerequisites.setValue(coursePrerequisiteRepository.findByCourse(course).stream()
                    .map(CoursePrerequisite::getPrerequisiteCourse)
                    .collect(Collectors.toSet()));
        }
    }

    private void setFormEnabled(boolean enabled) {
        courseCode.setEnabled(enabled);
        title.setEnabled(enabled);
        credits.setEnabled(enabled);
        department.setEnabled(enabled);
        prerequisites.setEnabled(enabled);
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
            courseService.save(binder.getBean(), prerequisites.getValue());
        } catch (PrerequisiteCycleException e) {
            // Can happen even though the picker hides these, e.g. if someone else added a link
            // after this form was opened. Keep the form open so the selection can be fixed.
            Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        Notification.show("Course saved").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        grid.asSingleSelect().clear();
        editCourse(null);
        refreshGrid();
    }

    private void delete() {
        Course course = binder.getBean();
        if (course == null || course.getId() == null) {
            return;
        }
        courseService.delete(course);
        Notification.show("Course deleted").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        grid.asSingleSelect().clear();
        editCourse(null);
        refreshGrid();
    }

    private void refreshGrid() {
        allCourses = courseRepository.findAll().stream()
                .sorted(Comparator.comparing(Course::getCourseCode))
                .toList();
        prerequisiteCodes = coursePrerequisiteRepository.findAll().stream()
                .sorted(Comparator.comparing((CoursePrerequisite link) -> link.getPrerequisiteCourse().getCourseCode()))
                .collect(Collectors.groupingBy(link -> link.getCourse().getId(),
                        Collectors.mapping(link -> link.getPrerequisiteCourse().getCourseCode(),
                                Collectors.joining(", "))));
        grid.setItems(allCourses);
    }
}
