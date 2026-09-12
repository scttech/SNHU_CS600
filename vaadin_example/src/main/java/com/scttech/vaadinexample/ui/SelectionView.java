package com.scttech.vaadinexample.ui;

import java.util.List;

import com.scttech.vaadinexample.ui.component.Showcase;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "selection", layout = MainLayout.class)
@PageTitle("Selection")
public class SelectionView extends VerticalLayout {

    private static final List<String> SIZES = List.of("Small", "Medium", "Large", "X-Large");

    public SelectionView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("60em");

        add(new H2("Selection"));
        add(checkboxes(), radioAndSelect(), comboBoxes(), listBox());
    }

    private FlexLayout row(Component... components) {
        FlexLayout layout = new FlexLayout(components);
        layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        layout.getStyle().set("gap", "var(--lumo-space-l)");
        return layout;
    }

    private Showcase checkboxes() {
        Checkbox single = new Checkbox("Subscribe to newsletter");

        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Toppings");
        group.setItems("Cheese", "Pepperoni", "Mushrooms", "Onions");
        group.select("Cheese");

        String code = """
                Checkbox single = new Checkbox("Subscribe to newsletter");

                CheckboxGroup<String> group = new CheckboxGroup<>();
                group.setLabel("Toppings");
                group.setItems("Cheese", "Pepperoni", "Mushrooms", "Onions");
                group.select("Cheese");
                """;

        return new Showcase("Checkbox & CheckboxGroup", row(single, group), code);
    }

    private Showcase radioAndSelect() {
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel("Shirt size");
        radioGroup.setItems(SIZES);
        radioGroup.setValue("Medium");

        Select<String> select = new Select<>();
        select.setLabel("Shipping method");
        select.setItems("Standard", "Express", "Overnight");
        select.setValue("Standard");

        String code = """
                RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
                radioGroup.setLabel("Shirt size");
                radioGroup.setItems("Small", "Medium", "Large", "X-Large");
                radioGroup.setValue("Medium");

                Select<String> select = new Select<>();
                select.setLabel("Shipping method");
                select.setItems("Standard", "Express", "Overnight");
                select.setValue("Standard");
                """;

        return new Showcase("RadioButtonGroup & Select", row(radioGroup, select), code);
    }

    private Showcase comboBoxes() {
        ComboBox<String> comboBox = new ComboBox<>("Country");
        comboBox.setItems("United States", "Canada", "United Kingdom", "Germany", "Japan");

        MultiSelectComboBox<String> multiSelect = new MultiSelectComboBox<>("Skills");
        multiSelect.setItems("Java", "Spring", "Vaadin", "SQL", "Docker");
        multiSelect.select("Java", "Vaadin");

        String code = """
                ComboBox<String> comboBox = new ComboBox<>("Country");
                comboBox.setItems("United States", "Canada", "United Kingdom", "Germany", "Japan");

                MultiSelectComboBox<String> multiSelect = new MultiSelectComboBox<>("Skills");
                multiSelect.setItems("Java", "Spring", "Vaadin", "SQL", "Docker");
                multiSelect.select("Java", "Vaadin");
                """;

        return new Showcase("ComboBox & MultiSelectComboBox",
                "Both filter as you type; MultiSelectComboBox additionally lets a user pick more "
                        + "than one value.",
                row(comboBox, multiSelect), code);
    }

    private Showcase listBox() {
        ListBox<String> listBox = new ListBox<>();
        listBox.setItems("Draft", "In review", "Approved", "Published");
        listBox.setValue("Draft");

        String code = """
                ListBox<String> listBox = new ListBox<>();
                listBox.setItems("Draft", "In review", "Approved", "Published");
                listBox.setValue("Draft");
                """;

        return new Showcase("ListBox", listBox, code);
    }
}
