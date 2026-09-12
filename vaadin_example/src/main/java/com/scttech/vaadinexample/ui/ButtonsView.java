package com.scttech.vaadinexample.ui;

import com.scttech.vaadinexample.ui.component.Showcase;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "buttons", layout = MainLayout.class)
@PageTitle("Buttons")
public class ButtonsView extends VerticalLayout {

    public ButtonsView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("60em");

        add(new H2("Buttons"));
        add(variants(), iconButtons(), sizesAndStates());
    }

    private Showcase variants() {
        Button primary = new Button("Primary");
        primary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button normal = new Button("Normal");

        Button tertiary = new Button("Tertiary");
        tertiary.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button success = new Button("Success");
        success.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button error = new Button("Error");
        error.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button contrast = new Button("Contrast");
        contrast.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        String code = """
                Button primary = new Button("Primary");
                primary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                Button normal = new Button("Normal");

                Button tertiary = new Button("Tertiary");
                tertiary.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

                Button success = new Button("Success");
                success.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

                Button error = new Button("Error");
                error.addThemeVariants(ButtonVariant.LUMO_ERROR);

                Button contrast = new Button("Contrast");
                contrast.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
                """;

        return new Showcase("Theme variants",
                "ButtonVariant controls the visual weight of a button: use PRIMARY for the one "
                        + "main action on a screen, TERTIARY for low-emphasis actions, and "
                        + "SUCCESS/ERROR to signal outcome.",
                new HorizontalLayout(primary, normal, tertiary, success, error, contrast), code);
    }

    private Showcase iconButtons() {
        Button leading = new Button("Save", VaadinIcon.CHECK.create());
        Button trailing = new Button("Next");
        trailing.setIconAfterText(true);
        trailing.setIcon(VaadinIcon.ARROW_RIGHT.create());
        Button iconOnly = new Button(VaadinIcon.TRASH.create());
        iconOnly.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        iconOnly.getElement().setAttribute("aria-label", "Delete");

        String code = """
                Button leading = new Button("Save", VaadinIcon.CHECK.create());

                Button trailing = new Button("Next");
                trailing.setIconAfterText(true);
                trailing.setIcon(VaadinIcon.ARROW_RIGHT.create());

                Button iconOnly = new Button(VaadinIcon.TRASH.create());
                iconOnly.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
                iconOnly.getElement().setAttribute("aria-label", "Delete");
                """;

        return new Showcase("Icons",
                "An icon-only button needs an aria-label since it has no visible text for "
                        + "screen readers.",
                new HorizontalLayout(leading, trailing, iconOnly), code);
    }

    private Showcase sizesAndStates() {
        Button large = new Button("Large");
        large.addThemeVariants(ButtonVariant.LUMO_LARGE);

        Button normal = new Button("Normal");

        Button small = new Button("Small");
        small.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button disabled = new Button("Disabled");
        disabled.setEnabled(false);

        String code = """
                Button large = new Button("Large");
                large.addThemeVariants(ButtonVariant.LUMO_LARGE);

                Button normal = new Button("Normal");

                Button small = new Button("Small");
                small.addThemeVariants(ButtonVariant.LUMO_SMALL);

                Button disabled = new Button("Disabled");
                disabled.setEnabled(false);
                """;

        return new Showcase("Sizes & disabled state", new HorizontalLayout(large, normal, small, disabled), code);
    }
}
