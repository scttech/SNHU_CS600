package com.scttech.cs600.module7.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;

/**
 * The "who's signed in, and how do they leave" row every authenticated view shares — extracted
 * once {@link DashboardView} needed the same header {@link CourseView} already had.
 */
public class AppHeader extends HorizontalLayout {

    public AppHeader(AuthenticationContext authenticationContext) {
        MenuBar navMenu = new MenuBar();
        navMenu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);

        MenuItem dashboard = navMenu.addItem("Dashboard");
        dashboard.addClickListener(e -> UI.getCurrent().navigate(DashboardView.class));

        MenuItem manage = navMenu.addItem("Manage");
        SubMenu manageSub = manage.getSubMenu();
        manageSub.addItem("Courses", e -> UI.getCurrent().navigate(CourseView.class));
        manageSub.addItem("Departments", e -> UI.getCurrent().navigate(DepartmentView.class));
        manageSub.addItem("Faculty & Staff", e -> UI.getCurrent().navigate(EmployeeView.class));
        manageSub.addItem("Students", e -> UI.getCurrent().navigate(StudentView.class));

        String signedInAs = authenticationContext.getPrincipalName().orElse("unknown user");
        Avatar avatar = new Avatar(signedInAs);
        avatar.addThemeVariants(AvatarVariant.LUMO_SMALL);

        MenuBar accountMenu = new MenuBar();
        accountMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        MenuItem account = accountMenu.addItem(avatar);
        SubMenu accountSub = account.getSubMenu();
        accountSub.addItem("Signed in as " + signedInAs).setEnabled(false);
        accountSub.addSeparator();
        accountSub.addItem(withIcon(VaadinIcon.USER, "Profile"), e -> {});
        accountSub.addItem(withIcon(VaadinIcon.COG, "Settings"), e -> {});
        accountSub.addSeparator();
        accountSub.addItem(withIcon(VaadinIcon.SIGN_OUT, "Sign out"), e -> {
            authenticationContext.logout();
        });

        setWidthFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        getStyle()
                .set("padding", "var(--lumo-space-s) var(--lumo-space-l)")
                .set("background-color", "var(--lumo-base-color)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");
        add(navMenu, accountMenu);
    }

    private HorizontalLayout withIcon(VaadinIcon icon, String text) {
        HorizontalLayout layout = new HorizontalLayout(icon.create(), new Span(text));
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(true);
        return layout;
    }
}
