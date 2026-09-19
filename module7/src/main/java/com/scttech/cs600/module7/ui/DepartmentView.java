package com.scttech.cs600.module7.ui;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.repository.department.DepartmentRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.component.textfield.TextField;



import jakarta.annotation.security.PermitAll;

@Route("departments")
@PageTitle("Departments")
@PermitAll
public class DepartmentView extends VerticalLayout {
    private final DepartmentRepository departmentRepository;

    private final Grid<Department> grid = new Grid<>(Department.class, false);
    private final Binder<Department> binder = new BeanValidationBinder<>(Department.class);

    private final TextField code = new TextField("Code");
    private final TextField name = new TextField("Name");
    private final TextField description = new TextField("Description");

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button addNew = new Button("Add Department");

    @SuppressWarnings("null")
    public DepartmentView(DepartmentRepository departmentRepository, AuthenticationContext authenticationContext) {
        this.departmentRepository = departmentRepository;
        setSizeFull();

        grid.addColumn(Department::getCode).setHeader("Code").setAutoWidth(true);
        grid.addColumn(Department::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Department::getDescription).setHeader("Description").setAutoWidth(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> editDepartment(event.getValue()));

        binder.forField(code).bind(Department::getCode, Department::setCode);
        binder.forField(name).bind(Department::getName, Department::setName);
        binder.forField(description).bind(Department::getDescription, Department::setDescription);

        save.addClickListener(event -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> delete());
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(event -> editDepartment(null));
        addNew.addClickListener(event -> {
            grid.asSingleSelect().clear();
            editDepartment(new Department("", "", ""));
        });

        VerticalLayout form = new VerticalLayout(code, name, description,
            new HorizontalLayout(save, delete, cancel)
        );
        form.setWidth("20em");

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);

        add(new AppHeader("Departments", authenticationContext), new RouterLink("← Dashboard", DashboardView.class), addNew, content);

        refreshGrid();
        editDepartment(null);
    }

    private void editDepartment(Department department) {
        binder.setBean(department);
        setFormEnabled(department != null);
    }

    private void setFormEnabled(boolean enabled) {
        code.setEnabled(enabled);
        name.setEnabled(enabled);
        description.setEnabled(enabled);
        save.setEnabled(enabled);
        cancel.setEnabled(enabled);
        delete.setEnabled(enabled && binder.getBean().getId() != null);
    }

    private void save() {
        if (!binder.isValid()) {
            binder.validate();
            return;
        }
        departmentRepository.save(binder.getBean());
        Notification.show("Department saved").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        grid.asSingleSelect().clear();
        editDepartment(null);
        refreshGrid();
    }

    private void delete() {
        Department department = binder.getBean();
        if (department == null || department.getId() == null) {
            return;
        }
        departmentRepository.delete(department);
        Notification.show("Department deleted").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        grid.asSingleSelect().clear();
        editDepartment(null);
        refreshGrid();
    }    

    private void refreshGrid() {
        grid.setItems(departmentRepository.findAll());
    }
}
