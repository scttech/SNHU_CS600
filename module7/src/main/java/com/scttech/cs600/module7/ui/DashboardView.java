package com.scttech.cs600.module7.ui;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
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

        var welcomeMessage = new H3("Welcome to the Registrar Dashboard");

        add(new AppHeader(authenticationContext), welcomeMessage);
    }
}
