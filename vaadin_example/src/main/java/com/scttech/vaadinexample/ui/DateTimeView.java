package com.scttech.vaadinexample.ui;

import java.time.LocalDate;
import java.time.LocalTime;

import com.scttech.vaadinexample.ui.component.Showcase;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "date-time", layout = MainLayout.class)
@PageTitle("Date & time")
public class DateTimeView extends VerticalLayout {

    public DateTimeView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("60em");

        add(new H2("Date & time"));
        add(pickers());
    }

    private FlexLayout row(Component... components) {
        FlexLayout layout = new FlexLayout(components);
        layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        layout.getStyle().set("gap", "var(--lumo-space-l)");
        return layout;
    }

    private Showcase pickers() {
        DatePicker datePicker = new DatePicker("Start date");
        datePicker.setValue(LocalDate.now());

        TimePicker timePicker = new TimePicker("Start time");
        timePicker.setValue(LocalTime.of(9, 0));
        timePicker.setStep(java.time.Duration.ofMinutes(15));

        DateTimePicker dateTimePicker = new DateTimePicker("Appointment");
        dateTimePicker.setValue(LocalDate.now().plusDays(1).atTime(14, 30));

        String code = """
                DatePicker datePicker = new DatePicker("Start date");
                datePicker.setValue(LocalDate.now());

                TimePicker timePicker = new TimePicker("Start time");
                timePicker.setValue(LocalTime.of(9, 0));
                timePicker.setStep(Duration.ofMinutes(15));

                DateTimePicker dateTimePicker = new DateTimePicker("Appointment");
                dateTimePicker.setValue(LocalDate.now().plusDays(1).atTime(14, 30));
                """;

        return new Showcase("DatePicker, TimePicker & DateTimePicker",
                row(datePicker, timePicker, dateTimePicker), code);
    }
}
