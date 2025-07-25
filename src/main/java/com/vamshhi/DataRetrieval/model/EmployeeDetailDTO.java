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
public class EmployeeDetailDTO {
    private Long id;
    private String name;
    private String email;
    private String departmentName;
    private LocalDate dateOfJoining;
    private Double salary;
    private String managerName;
    private List<ProjectDTO> projects;
    private List<PerformanceReviewDTO> last3PerformanceReviews;



}
