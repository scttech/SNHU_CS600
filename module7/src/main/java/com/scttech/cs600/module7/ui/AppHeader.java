package com.scttech.cs600.module7.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;

/**
 * The "who's signed in, and how do they leave" row every authenticated view shares — extracted
 * once {@link DashboardView} needed the same header {@link CourseView} already had.
 */
public class AppHeader extends HorizontalLayout {

    public AppHeader(String title, AuthenticationContext authenticationContext) {
        String signedInAs = authenticationContext.getPrincipalName().orElse("unknown user");
        Button logout = new Button("Log out", event -> authenticationContext.logout());

        add(new H2(title), new Span("Signed in as " + signedInAs), logout);
        setAlignItems(Alignment.CENTER);
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.BETWEEN);
    }
}
