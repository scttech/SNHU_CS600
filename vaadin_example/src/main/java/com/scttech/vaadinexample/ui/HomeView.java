package com.scttech.vaadinexample.ui;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Landing page explaining what this project is for. Unlike {@code module5}, which showcases a
 * single CRUD screen backed by a real database, this project is UI-only: every view below is a
 * catalog of Vaadin components with no persistence behind it, meant purely as a reference.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Vaadin Component Showcase")
public class HomeView extends VerticalLayout {

    public HomeView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("50em");

        add(new H2("Vaadin Component Showcase"));
        add(new Paragraph(
                "This project is a reference catalog of Vaadin components and layouts, built for "
                        + "developers who want to see how a component looks and behaves before wiring it "
                        + "into their own screens. It has no database and no REST API of its own — every "
                        + "example below runs entirely in memory."));

        add(new Paragraph("How to use it:"));
        UnorderedList list = new UnorderedList(
                new ListItem("Pick a category from the menu on the left."),
                new ListItem("Each example shows the live component alongside a \"View source\" panel."),
                new ListItem("Expand \"View source\" and use \"Copy code\" to grab the exact snippet."),
                new ListItem("Paste it into your own view and adjust bindings, data, and styling as needed."));
        add(list);

    }
}
