package com.scttech.vaadinexample.ui.component;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * A collapsible "View source" panel plus a "Copy code" button, so a developer browsing the
 * showcase can lift a snippet straight into their own project. The clipboard write happens
 * client-side via {@code navigator.clipboard}, since Flow has no server-side clipboard API.
 */
public class CodeBlock extends Div {

    public CodeBlock(String code) {
        Pre pre = new Pre(new Text(code));
        pre.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-m)")
                .set("margin", "0")
                .set("overflow-x", "auto")
                .set("font-size", "0.875em")
                .set("white-space", "pre");

        Button copy = new Button("Copy code", VaadinIcon.COPY.create());
        copy.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        copy.addClickListener(event -> {
            copy.getElement().executeJs("navigator.clipboard.writeText($0)", code);
            Notification notification = Notification.show("Code copied to clipboard", 2000,
                    Notification.Position.BOTTOM_END);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        VerticalLayout content = new VerticalLayout(new HorizontalLayout(copy), pre);
        content.setPadding(false);
        content.setSpacing(false);
        content.setWidthFull();

        Details details = new Details("View source", content);
        details.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.SMALL);
        details.setWidthFull();

        add(details);
    }
}
