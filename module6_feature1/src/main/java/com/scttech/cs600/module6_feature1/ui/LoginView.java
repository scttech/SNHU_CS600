package com.scttech.cs600.module6_feature1.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * The login screen from docs/module6_feature1/wireframe/login-screen.puml. {@link LoginForm} is
 * Vaadin's own component for exactly this — it already renders the "Incorrect username or
 * password" banner the wireframe shows, so there's no custom error-message UI to build here, only
 * to wire up: {@link #beforeEnter} turns the {@code ?error} query parameter Spring Security's
 * failed-login redirect adds into {@link LoginForm#setError}.
 */
@Route("login")
@PageTitle("Sign in")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Posts a plain HTML form to Spring Security's /login processing URL instead of going
        // through Vaadin's own client-server RPC — required for LoginForm to work with Spring
        // Security's form login at all.
        loginForm.setAction("login");

        add(new H1("Course Catalog"), loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        loginForm.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
