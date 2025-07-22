package com.vamshhi.DataRetrieval.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeListDTO {
    private Long id;
    private String name;
    private String email;
    private String departmentName; // Only department name, not the whole Department object
    private LocalDate dateOfJoining;
    private Double salary;

}
