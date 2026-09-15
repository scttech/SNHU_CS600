package com.scttech.cs600.module7.ui;

import com.scttech.cs600.module7.model.Course;
import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.repository.DepartmentRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Minus.Horizontal;
import com.vaadin.flow.data.binder.BeanValidationBinder;


import jakarta.annotation.security.PermitAll;

@Route("departments")
@PageTitle("Departments")
@PermitAll
public class DepartmentView extends VerticalLayout {
    private final DepartmentRepository departmentRepository;

    private final Grid<Department> grid = new Grid<>(Department.class, false);
    private final Binder<Department> binder = new BeanValidationBinder<>(Department.class);

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

        HorizontalLayout content = new HorizontalLayout(grid);
        content.setSizeFull();
        content.setFlexGrow(2, grid);

        add(new AppHeader("Departments", authenticationContext), new RouterLink("← Dashboard", DashboardView.class), addNew, content);

        refreshGrid();
    }

    private void editDepartment(Department department) {
        binder.setBean(department);
        setFormEnabled(department != null);
    }

    private void setFormEnabled(boolean enabled) {

    }

    private void refreshGrid() {
        grid.setItems(departmentRepository.findAll());
    }
}
