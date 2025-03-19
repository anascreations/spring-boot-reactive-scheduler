package com.job.scheduler.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("job_executions")
public class JobExecution {
	@Id
	private Long id;
	@Column("job_id")
	private Long jobId;
	private ExecutionStatus status;
	@Column("start_time")
	private LocalDateTime startTime;
	@Column("end_time")
	private LocalDateTime endTime;
	@Column("duration_ms")
	private Long durationMs;
	private String result;
	private String error;

	public enum ExecutionStatus {
		RUNNING, COMPLETED, FAILED, CANCELLED
	}
}
