package com.job.scheduler.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.job.scheduler.model.Job;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface JobRepository extends ReactiveCrudRepository<Job, Long> {
	Flux<Job> findByStatus(Job.JobStatus status);

	@Query("SELECT * FROM jobs WHERE next_run_at <= :now AND status = 'SCHEDULED'")
	Flux<Job> findJobsDueForExecution(LocalDateTime now);

	@Modifying
	@Query("UPDATE jobs SET status = :status, last_run_at = :lastRunAt, next_run_at = :nextRunAt, updated_at = :updatedAt WHERE id = :id")
	Mono<Integer> updateJobStatus(Long id, Job.JobStatus status, LocalDateTime lastRunAt, LocalDateTime nextRunAt,
			LocalDateTime updatedAt);
}