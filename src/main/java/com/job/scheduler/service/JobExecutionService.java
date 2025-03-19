package com.job.scheduler.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.job.scheduler.model.JobExecution;
import com.job.scheduler.repository.JobExecutionRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class JobExecutionService {
	private final JobExecutionRepository jobExecutionRepository;

	public Flux<JobExecution> getJobExecutions(Long jobId) {
		return jobExecutionRepository.findByJobIdOrderByStartTimeDesc(jobId);
	}

	public Flux<JobExecution> getRecentJobExecutions(Long jobId, int limit) {
		return jobExecutionRepository.findRecentExecutions(jobId, limit);
	}

	public Mono<JobExecution> startExecution(Long jobId) {
		JobExecution execution = JobExecution.builder().jobId(jobId).status(JobExecution.ExecutionStatus.RUNNING)
				.startTime(LocalDateTime.now()).build();
		return jobExecutionRepository.save(execution);
	}

	public Mono<JobExecution> completeExecution(Long executionId, String result) {
		return jobExecutionRepository.findById(executionId).flatMap(execution -> {
			LocalDateTime now = LocalDateTime.now();
			execution.setStatus(JobExecution.ExecutionStatus.COMPLETED);
			execution.setEndTime(now);
			execution.setResult(result);
			execution.setDurationMs(ChronoUnit.MILLIS.between(execution.getStartTime(), now));
			return jobExecutionRepository.save(execution);
		});
	}

	public Mono<JobExecution> failExecution(Long executionId, String error) {
		return jobExecutionRepository.findById(executionId).flatMap(execution -> {
			LocalDateTime now = LocalDateTime.now();
			execution.setStatus(JobExecution.ExecutionStatus.FAILED);
			execution.setEndTime(now);
			execution.setError(error);
			execution.setDurationMs(ChronoUnit.MILLIS.between(execution.getStartTime(), now));
			return jobExecutionRepository.save(execution);
		});
	}

	public Mono<JobExecution> cancelExecution(Long executionId) {
		return jobExecutionRepository.findById(executionId).flatMap(execution -> {
			LocalDateTime now = LocalDateTime.now();
			execution.setStatus(JobExecution.ExecutionStatus.CANCELLED);
			execution.setEndTime(now);
			execution.setDurationMs(ChronoUnit.MILLIS.between(execution.getStartTime(), now));
			return jobExecutionRepository.save(execution);
		});
	}
}
