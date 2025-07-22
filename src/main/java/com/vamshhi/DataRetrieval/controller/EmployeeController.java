package com.vamshhi.DataRetrieval.controller;

import com.vamshhi.DataRetrieval.entity.Employee;
import com.vamshhi.DataRetrieval.model.EmployeeDetailDTO;
import com.vamshhi.DataRetrieval.model.EmployeeListDTO;
import com.vamshhi.DataRetrieval.service.EmployeeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeListDTO>> getEmployees( // Changed return type
                                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reviewDate,
                                                               @RequestParam(required = false) Integer performanceScore,
                                                               @RequestParam(required = false) List<String> departmentNames,
                                                               @RequestParam(required = false) List<String> projectNames) {

        List<EmployeeListDTO> employees = employeeService.getEmployeesWithFilters( // Changed variable type
                reviewDate, performanceScore, departmentNames, projectNames);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<EmployeeDetailDTO> getEmployeeDetails(@PathVariable Long id) {
        return employeeService.getEmployeeDetails(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found with ID: " + id));
    }
    
}