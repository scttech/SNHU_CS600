package com.scttech.vaadinexample.ui;

import com.scttech.vaadinexample.ui.component.Showcase;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "layouts", layout = MainLayout.class)
@PageTitle("Layouts")
public class LayoutsView extends VerticalLayout {

    public LayoutsView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("60em");

        add(new H2("Layouts"));
        add(orderedLayouts(), formLayout(), splitLayout(), tabsAndDetails());
    }

    private Div box(String label) {
        Div div = new Div();
        div.setText(label);
        div.getStyle()
                .set("background", "var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-m)");
        return div;
    }

    private Showcase orderedLayouts() {
        HorizontalLayout horizontal = new HorizontalLayout(box("1"), box("2"), box("3"));

        VerticalLayout vertical = new VerticalLayout(box("A"), box("B"), box("C"));
        vertical.setPadding(false);

        String code = """
                HorizontalLayout horizontal = new HorizontalLayout(box("1"), box("2"), box("3"));

                VerticalLayout vertical = new VerticalLayout(box("A"), box("B"), box("C"));
                vertical.setPadding(false);
                """;

        return new Showcase("HorizontalLayout & VerticalLayout",
                "The two workhorse layouts: children flow left-to-right or top-to-bottom, sized "
                        + "and aligned with setFlexGrow/setAlignItems/setJustifyContentMode.",
                new VerticalLayout(horizontal, vertical), code);
    }

    private Showcase formLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));
        formLayout.add(new TextField("First name"), new TextField("Last name"));
        formLayout.add(new TextField("Address"), 2);

        String code = """
                FormLayout formLayout = new FormLayout();
                formLayout.setResponsiveSteps(
                        new FormLayout.ResponsiveStep("0", 1),
                        new FormLayout.ResponsiveStep("20em", 2));
                formLayout.add(new TextField("First name"), new TextField("Last name"));
                formLayout.add(new TextField("Address"), 2);
                """;

        return new Showcase("FormLayout",
                "Lays fields out in a responsive grid of columns; addFormItem(field, label) can "
                        + "also pair each field with its own label element. The last field spans both "
                        + "columns via its colspan argument.",
                formLayout, code);
    }

    private Showcase splitLayout() {
        SplitLayout splitLayout = new SplitLayout(box("Primary"), box("Secondary"));
        splitLayout.setSplitterPosition(30);
        splitLayout.setHeight("8em");

        String code = """
                SplitLayout splitLayout = new SplitLayout(box("Primary"), box("Secondary"));
                splitLayout.setSplitterPosition(30);
                splitLayout.setHeight("8em");
                """;

        return new Showcase("SplitLayout",
                "A user-draggable divider between two panels; setSplitterPosition sets the "
                        + "initial split as a percentage.",
                splitLayout, code);
    }

    private Showcase tabsAndDetails() {
        Tab overviewTab = new Tab("Overview");
        Tab detailsTab = new Tab("Details");
        Tabs tabs = new Tabs(overviewTab, detailsTab);

        Accordion accordion = new Accordion();
        accordion.add("Shipping", new Paragraph("Ships within 2 business days."));
        accordion.add("Returns", new Paragraph("30-day return window."));

        Details details = new Details("More info", new Paragraph("Collapsed by default."));

        String code = """
                Tab overviewTab = new Tab("Overview");
                Tab detailsTab = new Tab("Details");
                Tabs tabs = new Tabs(overviewTab, detailsTab);

                Accordion accordion = new Accordion();
                accordion.add("Shipping", new Paragraph("Ships within 2 business days."));
                accordion.add("Returns", new Paragraph("30-day return window."));

                Details details = new Details("More info", new Paragraph("Collapsed by default."));
                """;

        return new Showcase("Tabs, Accordion & Details",
                "Three different ways to progressively disclose content: Tabs switches between "
                        + "whole sections, Accordion lets several sections stay open independently, and "
                        + "Details is a single collapsible panel — the same component this showcase uses "
                        + "for its own \"View source\" panels.",
                new VerticalLayout(tabs, accordion, details), code);
    }
}
