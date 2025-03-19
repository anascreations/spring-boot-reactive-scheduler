package com.job.scheduler.dto;

import java.time.LocalDateTime;

import com.job.scheduler.model.Job;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {
	private Long id;
	@NotBlank(message = "Job name is required")
	private String name;
	@NotBlank(message = "Cron expression is required")
	private String cronExpression;
	@NotBlank(message = "Job class is required")
	private String jobClass;
	private String data;
	private Job.JobStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime lastRunAt;
	private LocalDateTime nextRunAt;
}
