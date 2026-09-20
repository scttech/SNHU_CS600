package com.scttech.cs600.module7.ui;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;

import jakarta.annotation.security.PermitAll;

/**
 * The post-login landing page (see docs/module7/README.md) — deliberately minimal, one
 * navigable tile to the Course Catalog, the only registrar tool this system has today. Future
 * features add more tiles here rather than each competing to own the {@code ""} route the way
 * {@link CourseView} used to.
 */
@Route("")
@PageTitle("Dashboard")
@PermitAll
public class DashboardView extends VerticalLayout {

    public DashboardView(AuthenticationContext authenticationContext) {
        setSizeFull();

        add(new AppHeader("Dashboard", authenticationContext));
        add(new H3("Registrar tools"));

        // A wrapping flex row rather than a HorizontalLayout, which never wraps: as tools are added
        // the tiles flow onto more lines on a narrow window instead of forcing a horizontal scroll.
        FlexLayout tiles = new FlexLayout(
                tool("Course Catalog", "Add, edit, and remove courses", CourseView.class),
                tool("Departments", "Add, edit, and remove departments", DepartmentView.class),
                tool("Faculty & Staff", "Add, edit, and remove faculty and staff members", EmployeeView.class),
                tool("Students", "Add, edit, and remove students", StudentView.class),
                comingSoon("Scheduling"));
        tiles.setFlexWrap(FlexWrap.WRAP);
        tiles.setWidthFull();
        tiles.getStyle().set("gap", "var(--lumo-space-m)");
        add(tiles);
    }

    private VerticalLayout tool(String title, String description, Class<? extends com.vaadin.flow.component.Component> view) {
        VerticalLayout tile = new VerticalLayout(new H3(title), new Paragraph(description), new RouterLink("Open", view));
        tile.setWidth("16em");
        return tile;
    }

    private VerticalLayout comingSoon(String title) {
        VerticalLayout tile = new VerticalLayout(new H3(title), new Paragraph("Coming in a future feature"));
        tile.setWidth("16em");
        return tile;
    }
}
