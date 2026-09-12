package com.scttech.vaadinexample.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

/**
 * Shell shared by every showcase view: a category menu in the drawer, built with
 * {@link SideNav}/{@link SideNavItem} — themselves an example of the components this project
 * showcases — and the selected view's content on the right.
 */
public class MainLayout extends AppLayout {

    public MainLayout() {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Vaadin Component Showcase");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        addToNavbar(toggle, title);
        addToDrawer(buildNav());
    }

    private SideNav buildNav() {
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Home", HomeView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem("Buttons", ButtonsView.class, VaadinIcon.HAND.create()));
        nav.addItem(new SideNavItem("Text inputs", TextInputsView.class, VaadinIcon.EDIT.create()));
        nav.addItem(new SideNavItem("Selection", SelectionView.class, VaadinIcon.CHECK_SQUARE.create()));
        nav.addItem(new SideNavItem("Date & time", DateTimeView.class, VaadinIcon.CALENDAR.create()));
        nav.addItem(new SideNavItem("Data display", DataDisplayView.class, VaadinIcon.TABLE.create()));
        nav.addItem(new SideNavItem("Layouts", LayoutsView.class, VaadinIcon.SPLIT.create()));
        nav.addItem(new SideNavItem("Feedback", FeedbackView.class, VaadinIcon.BELL.create()));
        nav.addItem(new SideNavItem("Forms & validation", FormView.class, VaadinIcon.FORM.create()));
        return nav;
    }
}
