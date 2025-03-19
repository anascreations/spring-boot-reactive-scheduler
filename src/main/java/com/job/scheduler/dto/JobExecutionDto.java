package com.job.scheduler.dto;

import java.time.LocalDateTime;

import com.job.scheduler.model.JobExecution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionDto {
	private Long id;
	private Long jobId;
	private JobExecution.ExecutionStatus status;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Long durationMs;
	private String result;
	private String error;
}
