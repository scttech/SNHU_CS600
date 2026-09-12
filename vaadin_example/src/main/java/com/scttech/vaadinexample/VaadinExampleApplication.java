package com.scttech.vaadinexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Vaadin 25 only auto-loads a theme (Aura) when no {@link AppShellConfigurator} is present; once
 * one is added — as it is here, to configure the app shell — the theme has to be requested
 * explicitly. This showcase is built entirely on Lumo's {@code ButtonVariant.LUMO_*} /
 * {@code NotificationVariant.LUMO_*} vocabulary, so it loads Lumo rather than the new Aura
 * default.
 */
@SpringBootApplication
@StyleSheet(Lumo.STYLESHEET)
public class VaadinExampleApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(VaadinExampleApplication.class, args);
	}

}
