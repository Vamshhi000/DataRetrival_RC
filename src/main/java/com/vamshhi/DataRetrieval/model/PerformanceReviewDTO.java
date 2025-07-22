package com.vamshhi.DataRetrieval.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceReviewDTO {
    private Long id;
    private LocalDate reviewDate;
    private Integer score;
    private String reviewComments;

}
