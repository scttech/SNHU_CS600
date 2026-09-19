package com.scttech.cs600.module7.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.repository.department.DepartmentRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@RequestMapping("/api/departments")
@Tag(name = "Departments", description = "Operations on the department catalog")
@SecurityRequirement(name = "basicAuth")
public class DepartmentController {

    private final DepartmentRepository depositRepository;

    public  DepartmentController(DepartmentRepository departmentRepository) {
        this.depositRepository = departmentRepository;
    }

    @GetMapping 
    @Operation(summary = "List all departments")
    public List<Department> findAll() {
        return depositRepository.findAll();
    }
    
}
