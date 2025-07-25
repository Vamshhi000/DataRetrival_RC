package com.vamshhi.DataRetrieval.repository;

import com.vamshhi.DataRetrieval.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByNameIn(List<String> names);
}
