package com.job.scheduler.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.job.scheduler.model.JobExecution;

import reactor.core.publisher.Flux;

@Repository
public interface JobExecutionRepository extends ReactiveCrudRepository<JobExecution, Long> {
	Flux<JobExecution> findByJobIdOrderByStartTimeDesc(Long jobId);

	@Query("SELECT * FROM job_executions WHERE job_id = :jobId ORDER BY start_time DESC LIMIT :limit")
	Flux<JobExecution> findRecentExecutions(Long jobId, int limit);
}
