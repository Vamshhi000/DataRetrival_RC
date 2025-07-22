package com.vamshhi.DataRetrieval.repository;

import com.vamshhi.DataRetrieval.entity.Employee;
import com.vamshhi.DataRetrieval.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployeeOrderByReviewDateDesc(Employee employee);
}
