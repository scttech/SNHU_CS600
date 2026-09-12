package com.scttech.cs600.module5.ui;

import com.scttech.cs600.module5.model.Course;
import com.scttech.cs600.module5.repository.CourseRepository;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Single-screen CRUD for the {@code courses} table only — later modules grow this into a
 * multi-view UI covering the rest of the registrar schema (see docs/module4). The bean validation
 * annotations on {@link Course} (e.g. {@code @NotBlank}, {@code @Min}) drive the form's validation
 * via {@link BeanValidationBinder}, so the rules live in one place instead of being duplicated here.
 */
@Route("")
@PageTitle("Courses")
public class CourseView extends VerticalLayout {

    private final CourseRepository courseRepository;

    private final Grid<Course> grid = new Grid<>(Course.class, false);
    private final TextField courseCode = new TextField("Course code");
    private final TextField title = new TextField("Title");
    private final IntegerField credits = new IntegerField("Credits");
    private final Binder<Course> binder = new BeanValidationBinder<>(Course.class);

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button addNew = new Button("Add course");

    public CourseView(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
        setSizeFull();

        grid.addColumn(Course::getCourseCode).setHeader("Course code").setAutoWidth(true);
        grid.addColumn(Course::getTitle).setHeader("Title").setAutoWidth(true);
        grid.addColumn(Course::getCredits).setHeader("Credits").setAutoWidth(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> editCourse(event.getValue()));

        binder.forField(courseCode).bind(Course::getCourseCode, Course::setCourseCode);
        binder.forField(title).bind(Course::getTitle, Course::setTitle);
        binder.forField(credits).bind(Course::getCredits, Course::setCredits);

        save.addClickListener(event -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> delete());
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(event -> editCourse(null));
        addNew.addClickListener(event -> {
            grid.asSingleSelect().clear();
            editCourse(new Course("", "", 1));
        });

        VerticalLayout form = new VerticalLayout(courseCode, title, credits,
                new HorizontalLayout(save, delete, cancel));
        form.setWidth("20em");

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);

        add(new H2("Courses"), addNew, content);

        refreshGrid();
        editCourse(null);
    }

    private void editCourse(Course course) {
        binder.setBean(course);
        setFormEnabled(course != null);
    }

    private void setFormEnabled(boolean enabled) {
        courseCode.setEnabled(enabled);
        title.setEnabled(enabled);
        credits.setEnabled(enabled);
        save.setEnabled(enabled);
        cancel.setEnabled(enabled);
        delete.setEnabled(enabled && binder.getBean().getId() != null);
    }

    private void save() {
        if (!binder.isValid()) {
            binder.validate();
            return;
        }
        courseRepository.save(binder.getBean());
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
        courseRepository.delete(course);
        Notification.show("Course deleted").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        grid.asSingleSelect().clear();
        editCourse(null);
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(courseRepository.findAll());
    }
}
