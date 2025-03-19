package com.job.scheduler.scheduling;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.job.scheduler.execution.JobExecutor;
import com.job.scheduler.execution.JobExecutorRegistry;
import com.job.scheduler.model.Job;
import com.job.scheduler.model.JobExecution;
import com.job.scheduler.service.JobExecutionService;
import com.job.scheduler.service.JobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduler {
	private final JobService jobService;
	private final JobExecutionService jobExecutionService;
	private final JobExecutorRegistry executorRegistry;

	@Scheduled(fixedDelay = 5000)
	public void pollAndExecuteJobs() {
		LocalDateTime now = LocalDateTime.now();
		log.debug("Polling for jobs due for execution at {}", now);
		jobService.findJobsDueForExecution(now).flatMap(this::processJob).subscribe(
				result -> log.debug("Job execution complete: {}", result),
				error -> log.error("Error executing jobs", error));
	}

	private Mono<String> processJob(Job job) {
		log.info("Processing job: {} - {}", job.getId(), job.getName());
		return jobService.markJobAsRunning(job.getId())
				.flatMap(runningJob -> jobExecutionService.startExecution(job.getId()).flatMap(execution -> {
					try {
						JobExecutor executor = executorRegistry.getExecutor(job);
						return executor.execute(job)
								.flatMap(result -> handleSuccessfulExecution(job, execution, result))
								.onErrorResume(error -> handleFailedExecution(job, execution, error));
					} catch (Exception e) {
						return handleFailedExecution(job, execution, e);
					}
				}));
	}

	private Mono<String> handleSuccessfulExecution(Job job, JobExecution execution, String result) {
		log.info("Job {} executed successfully", job.getId());
		return jobExecutionService.completeExecution(execution.getId(), result)
				.flatMap(completedExecution -> jobService.completeJob(job.getId()))
				.thenReturn("Job executed successfully: " + result);
	}

	private Mono<String> handleFailedExecution(Job job, JobExecution execution, Throwable error) {
		log.error("Job {} execution failed", job.getId(), error);
		return jobExecutionService.failExecution(execution.getId(), error.getMessage())
				.flatMap(failedExecution -> jobService.failJob(job.getId()))
				.thenReturn("Job execution failed: " + error.getMessage());
	}
}