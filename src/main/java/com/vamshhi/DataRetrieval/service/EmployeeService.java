package com.vamshhi.DataRetrieval.service;

import com.vamshhi.DataRetrieval.model.EmployeeDetailDTO;
import com.vamshhi.DataRetrieval.model.EmployeeListDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    public List<EmployeeListDTO> getEmployeesWithFilters(
            LocalDate reviewDate, Integer performanceScore, List<String> departmentNames, List<String> projectNames);
    public Optional<EmployeeDetailDTO> getEmployeeDetails(Long employeeId);

}
