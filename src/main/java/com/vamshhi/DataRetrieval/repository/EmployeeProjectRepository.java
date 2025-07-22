package com.vamshhi.DataRetrieval.repository;

import com.vamshhi.DataRetrieval.entity.EmployeeProject;
import com.vamshhi.DataRetrieval.entity.EmployeeProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, EmployeeProjectId> {
}
