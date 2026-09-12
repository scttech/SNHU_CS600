package com.scttech.cs600.module5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
public class Module5Application implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(Module5Application.class, args);
	}

}
