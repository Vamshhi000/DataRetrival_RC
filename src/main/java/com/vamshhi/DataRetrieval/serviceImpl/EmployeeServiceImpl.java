package com.vamshhi.DataRetrieval.serviceImpl;

import com.vamshhi.DataRetrieval.entity.*;
import com.vamshhi.DataRetrieval.model.EmployeeDetailDTO;
import com.vamshhi.DataRetrieval.model.EmployeeListDTO;
import com.vamshhi.DataRetrieval.model.PerformanceReviewDTO;
import com.vamshhi.DataRetrieval.model.ProjectDTO;
import com.vamshhi.DataRetrieval.repository.DepartmentRepository;
import com.vamshhi.DataRetrieval.repository.EmployeeRepository;
import com.vamshhi.DataRetrieval.repository.PerformanceReviewRepository;
import com.vamshhi.DataRetrieval.repository.ProjectRepository;
import com.vamshhi.DataRetrieval.service.EmployeeService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PerformanceReviewRepository performanceReviewRepository;


    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                           PerformanceReviewRepository performanceReviewRepository,
                           DepartmentRepository departmentRepository,
                           ProjectRepository projectRepository) {
        this.employeeRepository = employeeRepository;
        this.performanceReviewRepository = performanceReviewRepository;

    }

    @Transactional(readOnly = true)
    public List<EmployeeListDTO> getEmployeesWithFilters(
            LocalDate reviewDate, Integer performanceScore, List<String> departmentNames, List<String> projectNames) {
        Specification<Employee> spec = (root, query, cb) -> {
            query.distinct(true);
            return cb.conjunction();
        };

        if (reviewDate != null && performanceScore != null) {
            spec = spec.and((root, query, cb) -> {
                Join<Employee, PerformanceReview> prJoin = root.join("performanceReviews");
                return cb.and(
                        cb.equal(prJoin.get("reviewDate"), reviewDate),
                        cb.equal(prJoin.get("score"), performanceScore)
                );
            });
        }

        if (departmentNames != null && !departmentNames.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Employee, Department> deptJoin = root.join("department");
                return deptJoin.get("name").in(departmentNames);
            });
        }

        if (projectNames != null && !projectNames.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Employee, EmployeeProject> epJoin = root.join("employeeProjects", JoinType.INNER);
                Join<EmployeeProject, Project> projectJoin = epJoin.join("project");
                return projectJoin.get("name").in(projectNames);
            });
        }

        List<Employee> employees = employeeRepository.findAll(spec);

        return employees.stream().map(employee ->
                new EmployeeListDTO(
                        employee.getId(),
                        employee.getName(),
                        employee.getEmail(),
                        employee.getDepartment() != null ? employee.getDepartment().getName() : null,
                        employee.getDateOfJoining(),
                        employee.getSalary()
                )
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EmployeeDetailDTO> getEmployeeDetails(Long employeeId) {
        return employeeRepository.findById(employeeId).map(employee -> {
            EmployeeDetailDTO dto = new EmployeeDetailDTO();
            dto.setId(employee.getId());
            dto.setName(employee.getName());
            dto.setEmail(employee.getEmail());
            dto.setDateOfJoining(employee.getDateOfJoining());
            dto.setSalary(employee.getSalary());

            if (employee.getDepartment() != null) {
                dto.setDepartmentName(employee.getDepartment().getName());
            }
            if (employee.getManager() != null) {
                dto.setManagerName(employee.getManager().getName());
            }
            List<ProjectDTO> projectDTOs = employee.getEmployeeProjects().stream()
                    .map(ep -> new ProjectDTO(ep.getProject().getId(), ep.getProject().getName(),
                            ep.getProject().getStartDate(), ep.getProject().getEndDate(), ep.getRole()))
                    .collect(Collectors.toList());
            dto.setProjects(projectDTOs);
            List<PerformanceReviewDTO> reviewDTOs = performanceReviewRepository
                    .findByEmployeeOrderByReviewDateDesc(employee).stream()
                    .limit(3) // Get only the last 3
                    .map(pr -> new PerformanceReviewDTO(pr.getId(), pr.getReviewDate(), pr.getScore(), pr.getReviewComments()))
                    .collect(Collectors.toList());
            dto.setLast3PerformanceReviews(reviewDTOs);

            return dto;
        });

    }


}
