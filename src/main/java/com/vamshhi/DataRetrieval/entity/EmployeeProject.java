package com.vamshhi.DataRetrieval.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"employee", "project"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "employee_projects")
public class EmployeeProject {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private EmployeeProjectId id;

    @ManyToOne(fetch = FetchType.LAZY)


    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "assigned_date")
    private LocalDate assignedDate;

    private String role;

}
