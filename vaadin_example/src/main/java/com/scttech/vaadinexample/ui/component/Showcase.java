package com.scttech.vaadinexample.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * One example card: a heading, an optional description, a live demo of the component(s) being
 * showcased, and a {@link CodeBlock} with the exact source that built the demo. Every showcase
 * view on the site is just a stack of these.
 */
public class Showcase extends VerticalLayout {

    public Showcase(String title, Component demo, String sourceCode) {
        this(title, null, demo, sourceCode);
    }

    public Showcase(String title, String description, Component demo, String sourceCode) {
        setPadding(false);
        setSpacing(true);
        setWidthFull();
        getStyle().set("margin-bottom", "var(--lumo-space-l)");

        H4 heading = new H4(title);
        heading.getStyle().set("margin", "0");
        add(heading);

        if (description != null) {
            Paragraph desc = new Paragraph(description);
            desc.getStyle()
                    .set("margin", "0")
                    .set("color", "var(--lumo-secondary-text-color)");
            add(desc);
        }

        Div demoBox = new Div(demo);
        demoBox.setWidthFull();
        demoBox.getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("box-sizing", "border-box");
        add(demoBox);

        add(new CodeBlock(sourceCode));
    }
}
