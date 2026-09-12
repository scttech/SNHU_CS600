package com.scttech.vaadinexample.ui;

import com.scttech.vaadinexample.ui.component.Showcase;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.badge.Badge;
import com.vaadin.flow.component.badge.BadgeVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "data-display", layout = MainLayout.class)
@PageTitle("Data display")
public class DataDisplayView extends VerticalLayout {

    private record Product(String name, String category, double price, int stock) {
    }

    public DataDisplayView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("60em");

        add(new H2("Data display"));
        add(grid(), avatars(), iconsAndBadges());
    }

    private Showcase grid() {
        Grid<Product> grid = new Grid<>(Product.class, false);
        grid.addColumn(Product::name).setHeader("Name").setSortable(true);
        grid.addColumn(Product::category).setHeader("Category").setSortable(true);
        grid.addColumn(p -> String.format("$%.2f", p.price())).setHeader("Price");
        grid.addColumn(Product::stock).setHeader("In stock").setSortable(true);
        grid.setItems(
                new Product("Wireless mouse", "Accessories", 24.99, 150),
                new Product("Mechanical keyboard", "Accessories", 89.99, 60),
                new Product("27\" monitor", "Displays", 249.00, 20),
                new Product("USB-C hub", "Accessories", 39.50, 200));
        grid.setAllRowsVisible(true);

        String code = """
                record Product(String name, String category, double price, int stock) {}

                Grid<Product> grid = new Grid<>(Product.class, false);
                grid.addColumn(Product::name).setHeader("Name").setSortable(true);
                grid.addColumn(Product::category).setHeader("Category").setSortable(true);
                grid.addColumn(p -> String.format("$%.2f", p.price())).setHeader("Price");
                grid.addColumn(Product::stock).setHeader("In stock").setSortable(true);
                grid.setItems(
                        new Product("Wireless mouse", "Accessories", 24.99, 150),
                        new Product("Mechanical keyboard", "Accessories", 89.99, 60),
                        new Product("27\\" monitor", "Displays", 249.00, 20),
                        new Product("USB-C hub", "Accessories", 39.50, 200));
                grid.setAllRowsVisible(true);
                """;

        return new Showcase("Grid",
                "A column per property, with sortable headers. This is the same component "
                        + "module5's CourseView uses for its course list.",
                grid, code);
    }

    private Showcase avatars() {
        Avatar named = new Avatar("Ada Lovelace");

        Avatar withAbbreviation = new Avatar();
        withAbbreviation.setAbbreviation("JD");
        withAbbreviation.setColorIndex(2);

        AvatarGroup group = new AvatarGroup();
        group.setItems(
                new AvatarGroup.AvatarGroupItem("Grace Hopper"),
                new AvatarGroup.AvatarGroupItem("Alan Turing"),
                new AvatarGroup.AvatarGroupItem("Katherine Johnson"));
        group.setMaxItemsVisible(2);

        String code = """
                Avatar named = new Avatar("Ada Lovelace");

                Avatar withAbbreviation = new Avatar();
                withAbbreviation.setAbbreviation("JD");
                withAbbreviation.setColorIndex(2);

                AvatarGroup group = new AvatarGroup();
                group.setItems(
                        new AvatarGroup.AvatarGroupItem("Grace Hopper"),
                        new AvatarGroup.AvatarGroupItem("Alan Turing"),
                        new AvatarGroup.AvatarGroupItem("Katherine Johnson"));
                group.setMaxItemsVisible(2);
                """;

        return new Showcase("Avatar & AvatarGroup",
                new HorizontalLayout(named, withAbbreviation, group), code);
    }

    private Showcase iconsAndBadges() {
        Icon icon = VaadinIcon.STAR.create();
        icon.setColor("var(--lumo-primary-color)");

        Badge success = new Badge("Active");
        success.addThemeVariants(BadgeVariant.SUCCESS);

        Badge warning = new Badge("Pending");
        warning.addThemeVariants(BadgeVariant.WARNING);

        Badge error = new Badge("Failed");
        error.addThemeVariants(BadgeVariant.ERROR);

        String code = """
                Icon icon = VaadinIcon.STAR.create();
                icon.setColor("var(--lumo-primary-color)");

                Badge success = new Badge("Active");
                success.addThemeVariants(BadgeVariant.SUCCESS);

                Badge warning = new Badge("Pending");
                warning.addThemeVariants(BadgeVariant.WARNING);

                Badge error = new Badge("Failed");
                error.addThemeVariants(BadgeVariant.ERROR);
                """;

        return new Showcase("Icon & Badge", new HorizontalLayout(icon, success, warning, error), code);
    }
}
