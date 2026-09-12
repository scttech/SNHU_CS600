package com.scttech.vaadinexample.ui;

import com.scttech.vaadinexample.ui.component.Showcase;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "feedback", layout = MainLayout.class)
@PageTitle("Feedback")
public class FeedbackView extends VerticalLayout {

    public FeedbackView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("60em");

        add(new H2("Feedback"));
        add(notifications(), dialog(), confirmDialog(), progressBar());
    }

    private Showcase notifications() {
        Button success = new Button("Success", event -> {
            Notification notification = Notification.show("Saved successfully", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        Button error = new Button("Error", event -> {
            Notification notification = Notification.show("Something went wrong", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        String code = """
                Button success = new Button("Success", event -> {
                    Notification notification = Notification.show(
                            "Saved successfully", 3000, Notification.Position.TOP_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                });
                Button error = new Button("Error", event -> {
                    Notification notification = Notification.show(
                            "Something went wrong", 3000, Notification.Position.TOP_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                });
                """;

        return new Showcase("Notification",
                "A transient, non-blocking message. Position and duration are configurable; "
                        + "theme variants color it for the situation.",
                new HorizontalLayout(success, error), code);
    }

    private Showcase dialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit profile");
        dialog.add(new Paragraph("Dialog content goes here — typically a form."));
        Button closeButton = new Button("Close", event -> dialog.close());
        dialog.getFooter().add(closeButton);

        Button open = new Button("Open dialog", event -> dialog.open());

        String code = """
                Dialog dialog = new Dialog();
                dialog.setHeaderTitle("Edit profile");
                dialog.add(new Paragraph("Dialog content goes here — typically a form."));
                Button closeButton = new Button("Close", event -> dialog.close());
                dialog.getFooter().add(closeButton);

                Button open = new Button("Open dialog", event -> dialog.open());
                """;

        return new Showcase("Dialog", "A modal overlay for focused tasks that shouldn't navigate away from the current view.",
                open, code);
    }

    private Showcase confirmDialog() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Delete item?");
        confirmDialog.setText("This action cannot be undone.");
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmText("Delete");
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.addConfirmListener(event -> {
            Notification notification = Notification.show("Item deleted", 2000, Notification.Position.BOTTOM_END);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        Button open = new Button("Delete...", event -> confirmDialog.open());
        open.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        String code = """
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader("Delete item?");
                confirmDialog.setText("This action cannot be undone.");
                confirmDialog.setCancelable(true);
                confirmDialog.setConfirmText("Delete");
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.addConfirmListener(event -> {
                    Notification notification = Notification.show(
                            "Item deleted", 2000, Notification.Position.BOTTOM_END);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                });

                Button open = new Button("Delete...", event -> confirmDialog.open());
                open.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
                """;

        return new Showcase("ConfirmDialog", "A purpose-built dialog for confirm/cancel/reject decisions.", open, code);
    }

    private Showcase progressBar() {
        ProgressBar determinate = new ProgressBar(0, 100, 65);

        ProgressBar indeterminate = new ProgressBar();
        indeterminate.setIndeterminate(true);

        String code = """
                ProgressBar determinate = new ProgressBar(0, 100, 65);

                ProgressBar indeterminate = new ProgressBar();
                indeterminate.setIndeterminate(true);
                """;

        return new Showcase("ProgressBar", new VerticalLayout(determinate, indeterminate), code);
    }
}
