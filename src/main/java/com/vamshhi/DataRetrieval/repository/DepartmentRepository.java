package com.vamshhi.DataRetrieval.repository;

import com.vamshhi.DataRetrieval.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByNameIn(List<String> names);
}
