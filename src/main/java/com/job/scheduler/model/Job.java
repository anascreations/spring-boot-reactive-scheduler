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
@Table("jobs")
public class Job {
	@Id
	private Long id;
	private String name;
	@Column("cron_expression")
	private String cronExpression;
	@Column("job_class")
	private String jobClass;
	private String data;
	private JobStatus status;
	@Column("created_at")
	private LocalDateTime createdAt;
	@Column("updated_at")
	private LocalDateTime updatedAt;
	@Column("last_run_at")
	private LocalDateTime lastRunAt;
	@Column("next_run_at")
	private LocalDateTime nextRunAt;

	public enum JobStatus {
		SCHEDULED, RUNNING, PAUSED, COMPLETED, FAILED, CANCELLED
	}
}
