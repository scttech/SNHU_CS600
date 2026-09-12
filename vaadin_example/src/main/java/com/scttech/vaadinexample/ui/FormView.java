package com.scttech.vaadinexample.ui;

import com.scttech.vaadinexample.ui.component.Showcase;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * A full form wired up with {@link Binder}, the same pattern module5's CourseView uses to bind
 * fields to a bean — but here the "save" action is just a notification, since this project has
 * no repository or database to persist to.
 */
@Route(value = "form", layout = MainLayout.class)
@PageTitle("Forms & validation")
public class FormView extends VerticalLayout {

    /** Plain in-memory bean; nothing here is persisted. */
    public static class Signup {
        private String fullName = "";
        private String email = "";
        private Integer age;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    public FormView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("40em");

        add(new H2("Forms & validation"));
        add(signupForm());
    }

    private Showcase signupForm() {
        TextField fullName = new TextField("Full name");
        EmailField email = new EmailField("Email");
        IntegerField age = new IntegerField("Age");

        Binder<Signup> binder = new Binder<>(Signup.class);
        binder.forField(fullName)
                .withValidator(new StringLengthValidator("Full name must not be empty", 1, null))
                .bind(Signup::getFullName, Signup::setFullName);
        binder.forField(email)
                .withValidator(new EmailValidator("Enter a valid email address"))
                .bind(Signup::getEmail, Signup::setEmail);
        binder.forField(age)
                .withValidator(new IntegerRangeValidator("Age must be between 0 and 120", 0, 120))
                .bind(Signup::getAge, Signup::setAge);
        binder.setBean(new Signup());

        Button submit = new Button("Sign up");
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submit.addClickListener(event -> {
            try {
                Signup bean = new Signup();
                binder.writeBean(bean);
                Notification notification = Notification.show(
                        "Signed up " + bean.getFullName() + " (not actually saved anywhere)", 3000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (ValidationException e) {
                Notification.show("Fix the highlighted fields", 3000, Notification.Position.MIDDLE);
            }
        });

        FormLayout formLayout = new FormLayout(fullName, email, age, submit);
        formLayout.setColspan(submit, 2);

        String code = """
                public class Signup {
                    private String fullName = "";
                    private String email = "";
                    private Integer age;
                    // getters and setters ...
                }

                TextField fullName = new TextField("Full name");
                EmailField email = new EmailField("Email");
                IntegerField age = new IntegerField("Age");

                Binder<Signup> binder = new Binder<>(Signup.class);
                binder.forField(fullName)
                        .withValidator(new StringLengthValidator("Full name must not be empty", 1, null))
                        .bind(Signup::getFullName, Signup::setFullName);
                binder.forField(email)
                        .withValidator(new EmailValidator("Enter a valid email address"))
                        .bind(Signup::getEmail, Signup::setEmail);
                binder.forField(age)
                        .withValidator(new IntegerRangeValidator("Age must be between 0 and 120", 0, 120))
                        .bind(Signup::getAge, Signup::setAge);
                binder.setBean(new Signup());

                Button submit = new Button("Sign up");
                submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                submit.addClickListener(event -> {
                    try {
                        Signup bean = new Signup();
                        binder.writeBean(bean);
                        Notification.show("Signed up " + bean.getFullName());
                    } catch (ValidationException e) {
                        Notification.show("Fix the highlighted fields");
                    }
                });

                FormLayout formLayout = new FormLayout(fullName, email, age, submit);
                formLayout.setColspan(submit, 2);
                """;

        return new Showcase("Binder-driven form",
                "Binder wires fields to a bean and runs validators before the value ever reaches "
                        + "it; writeBean() throws ValidationException if anything fails, which is caught "
                        + "here instead of persisting anything. Compare this to module5's CourseView, "
                        + "which uses the same Binder pattern but saves the bean through a repository.",
                formLayout, code);
    }
}
