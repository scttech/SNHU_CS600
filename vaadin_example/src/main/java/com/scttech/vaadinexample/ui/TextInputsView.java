package com.scttech.vaadinexample.ui;

import com.scttech.vaadinexample.ui.component.Showcase;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "text-inputs", layout = MainLayout.class)
@PageTitle("Text inputs")
public class TextInputsView extends VerticalLayout {

    public TextInputsView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("60em");

        add(new H2("Text inputs"));
        add(basicFields(), numericFields(), helperAndPrefix());
    }

    private FlexLayout row(com.vaadin.flow.component.Component... fields) {
        FlexLayout layout = new FlexLayout(fields);
        layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        layout.getStyle().set("gap", "var(--lumo-space-m)");
        return layout;
    }

    private Showcase basicFields() {
        TextField textField = new TextField("Name");
        textField.setPlaceholder("Jane Doe");

        TextArea textArea = new TextArea("Notes");
        textArea.setPlaceholder("Free-form text across multiple lines");

        PasswordField passwordField = new PasswordField("Password");

        EmailField emailField = new EmailField("Email");
        emailField.setErrorMessage("Enter a valid email address");

        String code = """
                TextField textField = new TextField("Name");
                textField.setPlaceholder("Jane Doe");

                TextArea textArea = new TextArea("Notes");
                textArea.setPlaceholder("Free-form text across multiple lines");

                PasswordField passwordField = new PasswordField("Password");

                EmailField emailField = new EmailField("Email");
                emailField.setErrorMessage("Enter a valid email address");
                """;

        return new Showcase("Basic fields", row(textField, textArea, passwordField, emailField), code);
    }

    private Showcase numericFields() {
        IntegerField integerField = new IntegerField("Quantity");
        integerField.setMin(0);
        integerField.setStepButtonsVisible(true);

        NumberField numberField = new NumberField("Weight (kg)");
        numberField.setMin(0);

        BigDecimalField priceField = new BigDecimalField("Price");
        priceField.setPrefixComponent(VaadinIcon.DOLLAR.create());

        String code = """
                IntegerField integerField = new IntegerField("Quantity");
                integerField.setMin(0);
                integerField.setStepButtonsVisible(true);

                NumberField numberField = new NumberField("Weight (kg)");
                numberField.setMin(0);

                BigDecimalField priceField = new BigDecimalField("Price");
                priceField.setPrefixComponent(VaadinIcon.DOLLAR.create());
                """;

        return new Showcase("Numeric fields", row(integerField, numberField, priceField), code);
    }

    private Showcase helperAndPrefix() {
        TextField withHelper = new TextField("Username");
        withHelper.setHelperText("Must be unique");
        withHelper.setClearButtonVisible(true);

        TextField withPrefix = new TextField("Search");
        withPrefix.setPrefixComponent(VaadinIcon.SEARCH.create());
        withPrefix.setPlaceholder("Type to search...");

        TextField required = new TextField("Full name");
        required.setRequired(true);
        required.setRequiredIndicatorVisible(true);

        String code = """
                TextField withHelper = new TextField("Username");
                withHelper.setHelperText("Must be unique");
                withHelper.setClearButtonVisible(true);

                TextField withPrefix = new TextField("Search");
                withPrefix.setPrefixComponent(VaadinIcon.SEARCH.create());
                withPrefix.setPlaceholder("Type to search...");

                TextField required = new TextField("Full name");
                required.setRequired(true);
                required.setRequiredIndicatorVisible(true);
                """;

        return new Showcase("Helper text, clear button, prefix icon, required",
                row(withHelper, withPrefix, required), code);
    }
}
