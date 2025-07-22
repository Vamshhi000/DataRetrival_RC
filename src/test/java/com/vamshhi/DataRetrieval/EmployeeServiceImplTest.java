package com.vamshhi.DataRetrieval;


import com.vamshhi.DataRetrieval.entity.*;
import com.vamshhi.DataRetrieval.model.EmployeeDetailDTO;
import com.vamshhi.DataRetrieval.model.EmployeeListDTO;
import com.vamshhi.DataRetrieval.model.ProjectDTO;
import com.vamshhi.DataRetrieval.repository.DepartmentRepository;
import com.vamshhi.DataRetrieval.repository.EmployeeRepository;
import com.vamshhi.DataRetrieval.repository.PerformanceReviewRepository;
import com.vamshhi.DataRetrieval.repository.ProjectRepository;
import com.vamshhi.DataRetrieval.serviceImpl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Comparator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PerformanceReviewRepository performanceReviewRepository;

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;


    private Employee alice;
    private Employee bob;
    private Department engineering;
    private Department hr;
    private Project quantumLeap;
    private Project talentAcquisition;
    private PerformanceReview aliceReview1;
    private PerformanceReview aliceReview2;
    private PerformanceReview aliceReview3;
    private EmployeeProject aliceQuantumLeapEP;

    @BeforeEach
    void setUp() {
        engineering = new Department(1L, "Engineering", 1000000.0, new HashSet<>(), new HashSet<>());
        hr = new Department(2L, "HR", 500000.0, new HashSet<>(), new HashSet<>());
        alice = new Employee(1L, "Alice Wonderland", "alice.w@example.com", engineering, LocalDate.of(2018, 5, 10), 120000.0,
                null, new HashSet<>(), new HashSet<>(), new HashSet<>());
        bob = new Employee(2L, "Bob Builder", "bob.b@example.com", hr, LocalDate.of(2019, 1, 20), 95000.0,
                null, new HashSet<>(), new HashSet<>(), new HashSet<>());
        engineering.getEmployees().add(alice);
        hr.getEmployees().add(bob);
        aliceReview1 = new PerformanceReview(1L, alice, LocalDate.of(2025, 1, 15), 93, "Continues to be a top performer.");
        aliceReview2 = new PerformanceReview(2L, alice, LocalDate.of(2024, 7, 15), 95, "Consistently exceeds expectations.");
        aliceReview3 = new PerformanceReview(3L, alice, LocalDate.of(2024, 1, 15), 92, "Exceptional leadership.");
        PerformanceReview aliceReviewOld = new PerformanceReview(4L, alice, LocalDate.of(2023, 1, 15), 90, "Good work.");
        alice.getPerformanceReviews().addAll(Arrays.asList(aliceReview1, aliceReview2, aliceReview3, aliceReviewOld));
        quantumLeap = new Project(1L, "Project Quantum Leap", LocalDate.of(2024,1,1), LocalDate.of(2025,6,30), engineering, new HashSet<>());
        talentAcquisition = new Project(2L, "Talent Acquisition Drive", LocalDate.of(2024,2,1), LocalDate.of(2024,8,31), hr, new HashSet<>());
        engineering.getProjects().add(quantumLeap);
        hr.getProjects().add(talentAcquisition);
        EmployeeProjectId aliceQuantumLeapId = new EmployeeProjectId(alice.getId(), quantumLeap.getId());
        aliceQuantumLeapEP = new EmployeeProject(aliceQuantumLeapId, alice, quantumLeap, LocalDate.of(2024, 1, 5), "Project Lead");
        alice.getEmployeeProjects().add(aliceQuantumLeapEP);
        quantumLeap.getEmployeeProjects().add(aliceQuantumLeapEP);
        reset(employeeRepository, performanceReviewRepository);
        reset(departmentRepository, projectRepository);
    }

    @Test
    void getEmployeesWithFilters_combinedFilters_shouldReturnCorrectEmployee() {
        LocalDate reviewDate = LocalDate.of(2024, 1, 15);
        Integer performanceScore = 92;
        List<String> departmentNames = Collections.singletonList("Engineering");
        List<String> projectNames = Collections.singletonList("Project Quantum Leap");
        when(employeeRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(alice));

        List<EmployeeListDTO> result = employeeService.getEmployeesWithFilters(
                reviewDate, performanceScore, departmentNames, projectNames);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Alice Wonderland", result.get(0).getName());
        assertEquals("Engineering", result.get(0).getDepartmentName());
        verify(employeeRepository, times(1)).findAll(any(Specification.class));
    }
    @Test
    void getEmployeesWithFilters_withMultipleDepartmentNames_shouldReturnFilteredEmployees() {
        when(employeeRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(alice, bob));
        List<EmployeeListDTO> result = employeeService.getEmployeesWithFilters(null, null, Arrays.asList("Engineering", "HR"), null);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("Alice Wonderland")));
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("Bob Builder")));
        verify(employeeRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getEmployeesWithFilters_withMultipleProjectNames_shouldReturnFilteredEmployees() {
        when(employeeRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(alice, bob));
        List<EmployeeListDTO> result = employeeService.getEmployeesWithFilters(null, null, null, Arrays.asList("Project Quantum Leap", "Talent Acquisition Drive"));
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("Alice Wonderland")));
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("Bob Builder")));
        verify(employeeRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getEmployeesWithFilters_noResults_shouldReturnEmptyList() {
        when(employeeRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());
        List<EmployeeListDTO> result = employeeService.getEmployeesWithFilters(
                LocalDate.of(2000, 1, 1), 10, Collections.singletonList("NonExistent"), null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findAll(any(Specification.class));
    }


    @Test
    void getEmployeeDetails_employeeFound_shouldReturnDetailDTO() {

        Long employeeId = alice.getId();
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(alice));
        List<PerformanceReview> aliceReviewsSorted = alice.getPerformanceReviews().stream()
                .sorted(Comparator.comparing(PerformanceReview::getReviewDate).reversed())
                .collect(Collectors.toList());
        when(performanceReviewRepository.findByEmployeeOrderByReviewDateDesc(alice))
                .thenReturn(aliceReviewsSorted);
        Optional<EmployeeDetailDTO> result = employeeService.getEmployeeDetails(employeeId);
        assertTrue(result.isPresent());
        EmployeeDetailDTO dto = result.get();
        assertEquals(employeeId, dto.getId());
        assertEquals("Alice Wonderland", dto.getName());
        assertEquals("alice.w@example.com", dto.getEmail());
        assertEquals(LocalDate.of(2018, 5, 10), dto.getDateOfJoining());
        assertEquals(120000.0, dto.getSalary());
        assertEquals("Engineering", dto.getDepartmentName());
        assertNull(dto.getManagerName()); // Alice has no manager in this setup
        assertNotNull(dto.getProjects());
        assertEquals(1, dto.getProjects().size());
        ProjectDTO projectDTO = dto.getProjects().get(0);
        assertEquals(quantumLeap.getId(), projectDTO.getId());
        assertEquals("Project Quantum Leap", projectDTO.getName());
        assertEquals("Project Lead", projectDTO.getRole());
        assertNotNull(dto.getLast3PerformanceReviews());
        assertEquals(3, dto.getLast3PerformanceReviews().size());
        assertEquals(LocalDate.of(2025, 1, 15), dto.getLast3PerformanceReviews().get(0).getReviewDate());
        assertEquals(93, dto.getLast3PerformanceReviews().get(0).getScore());
        assertEquals(LocalDate.of(2024, 7, 15), dto.getLast3PerformanceReviews().get(1).getReviewDate());
        assertEquals(95, dto.getLast3PerformanceReviews().get(1).getScore());
        assertEquals(LocalDate.of(2024, 1, 15), dto.getLast3PerformanceReviews().get(2).getReviewDate());
        assertEquals(92, dto.getLast3PerformanceReviews().get(2).getScore());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(performanceReviewRepository, times(1)).findByEmployeeOrderByReviewDateDesc(alice);
    }


}