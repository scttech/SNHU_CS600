package com.scttech.vaadinexample.ui;

import com.scttech.vaadinexample.ui.component.Showcase;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "menus", layout = MainLayout.class)
@PageTitle("Menus")
public class MenusView extends VerticalLayout {

    public MenusView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("60em");

        add(new H2("Menus"));
        add(menuBar(), contextMenu(), userMenu());
    }

    private Showcase menuBar() {
        MenuBar menuBar = new MenuBar();

        var file = menuBar.addItem("File");
        var fileSub = file.getSubMenu();
        fileSub.addItem("New", e -> notify("New"));
        fileSub.addItem("Open", e -> notify("Open"));
        fileSub.addSeparator();
        fileSub.addItem("Exit", e -> notify("Exit"));

        var edit = menuBar.addItem("Edit");
        var editSub = edit.getSubMenu();
        editSub.addItem("Cut", e -> notify("Cut"));
        editSub.addItem("Copy", e -> notify("Copy"));
        editSub.addItem("Paste", e -> notify("Paste"));

        var view = menuBar.addItem("View");
        var viewSub = view.getSubMenu();
        MenuItem darkMode = viewSub.addItem("Dark mode", e -> notify("Dark mode toggled"));
        darkMode.setCheckable(true);
        MenuItem lineNumbers = viewSub.addItem("Line numbers", e -> notify("Line numbers toggled"));
        lineNumbers.setCheckable(true);
        lineNumbers.setChecked(true);

        MenuItem disabled = menuBar.addItem("Read-only action");
        disabled.setEnabled(false);

        String code = """
                MenuBar menuBar = new MenuBar();

                MenuItem file = menuBar.addItem("File");
                SubMenu fileSub = file.getSubMenu();
                fileSub.addItem("New", e -> ...);
                fileSub.addItem("Open", e -> ...);
                fileSub.addSeparator();
                fileSub.addItem("Exit", e -> ...);

                MenuItem edit = menuBar.addItem("Edit");
                SubMenu editSub = edit.getSubMenu();
                editSub.addItem("Cut", e -> ...);
                editSub.addItem("Copy", e -> ...);
                editSub.addItem("Paste", e -> ...);

                MenuItem view = menuBar.addItem("View");
                SubMenu viewSub = view.getSubMenu();
                MenuItem darkMode = viewSub.addItem("Dark mode", e -> ...);
                darkMode.setCheckable(true);
                MenuItem lineNumbers = viewSub.addItem("Line numbers", e -> ...);
                lineNumbers.setCheckable(true);
                lineNumbers.setChecked(true);

                MenuItem disabled = menuBar.addItem("Read-only action");
                disabled.setEnabled(false);
                """;

        return new Showcase("Menu bar with dropdowns",
                "MenuBar renders a row of top-level items; any item with a SubMenu shows a caret "
                        + "and opens a dropdown on click. Items can be checkable (a persistent toggle "
                        + "state, like \"Dark mode\") or disabled to signal an unavailable action.",
                menuBar, code);
    }

    private Showcase contextMenu() {
        Div target = new Div(new Span("Right-click (or long-press) this box"));
        target.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("width", "100%")
                .set("height", "6em")
                .set("border", "1px dashed var(--lumo-contrast-30pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("color", "var(--lumo-secondary-text-color)");

        ContextMenu contextMenu = new ContextMenu(target);
        contextMenu.addItem("Rename", e -> notify("Rename"));
        contextMenu.addItem("Duplicate", e -> notify("Duplicate"));
        var moreSub = contextMenu.addItem("More").getSubMenu();
        moreSub.addItem("Share", e -> notify("Share"));
        moreSub.addItem("Move to...", e -> notify("Move to..."));
        contextMenu.addSeparator();
        contextMenu.addItem("Delete", e -> notify("Delete"));

        String code = """
                Div target = new Div(new Span("Right-click (or long-press) this box"));

                ContextMenu contextMenu = new ContextMenu(target);
                contextMenu.addItem("Rename", e -> ...);
                contextMenu.addItem("Duplicate", e -> ...);
                SubMenu moreSub = contextMenu.addItem("More").getSubMenu();
                moreSub.addItem("Share", e -> ...);
                moreSub.addItem("Move to...", e -> ...);
                contextMenu.addSeparator();
                contextMenu.addItem("Delete", e -> ...);
                """;

        return new Showcase("Context menu",
                "ContextMenu attaches to any component and opens on right-click (or long-press on "
                        + "touch devices) instead of a click, which makes it a good fit for per-row or "
                        + "per-card actions in a grid or card list.",
                target, code);
    }

    private Showcase userMenu() {
        Avatar avatar = new Avatar("Ada Lovelace");
        avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);

        MenuBar userMenu = new MenuBar();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        var account = userMenu.addItem(avatar);
        var accountSub = account.getSubMenu();
        accountSub.addItem("Signed in as ada@example.com").setEnabled(false);
        accountSub.addSeparator();
        accountSub.addItem(withIcon(VaadinIcon.USER, "Profile"), e -> notify("Profile"));
        accountSub.addItem(withIcon(VaadinIcon.COG, "Settings"), e -> notify("Settings"));
        accountSub.addSeparator();
        accountSub.addItem(withIcon(VaadinIcon.SIGN_OUT, "Sign out"), e -> notify("Sign out"));

        String code = """
                Avatar avatar = new Avatar("Ada Lovelace");
                avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);

                MenuBar userMenu = new MenuBar();
                userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

                MenuItem account = userMenu.addItem(avatar);
                SubMenu accountSub = account.getSubMenu();
                accountSub.addItem("Signed in as ada@example.com").setEnabled(false);
                accountSub.addSeparator();
                accountSub.addItem(profileIcon, "Profile", e -> ...);
                accountSub.addItem(settingsIcon, "Settings", e -> ...);
                accountSub.addSeparator();
                accountSub.addItem(signOutIcon, "Sign out", e -> ...);
                """;

        return new Showcase("Avatar with user settings menu",
                "The common \"account menu\" pattern for an app header: put an Avatar as the "
                        + "content of a MenuBar item, style the bar with the tertiary-inline theme "
                        + "variant so it reads as a plain icon rather than a button, and put account "
                        + "actions in its sub-menu. A disabled item works well as a non-clickable "
                        + "label, like the signed-in email here.",
                new HorizontalLayout(userMenu), code);
    }

    private HorizontalLayout withIcon(VaadinIcon icon, String text) {
        HorizontalLayout layout = new HorizontalLayout(icon.create(), new Span(text));
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(true);
        return layout;
    }

    private void notify(String action) {
        Notification.show(action, 1500, Notification.Position.BOTTOM_START);
    }
}
