package com.job.scheduler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.job.scheduler.model.JobExecution;
import com.job.scheduler.service.JobExecutionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api/jobs/{jobId}/executions")
@RequiredArgsConstructor
public class JobExecutionController {
	private final JobExecutionService jobExecutionService;

	@GetMapping
	public Flux<JobExecution> getJobExecutions(@PathVariable Long jobId) {
		return jobExecutionService.getJobExecutions(jobId);
	}

	@GetMapping("recent")
	public Flux<JobExecution> getRecentJobExecutions(@PathVariable Long jobId,
			@RequestParam(defaultValue = "10") int limit) {
		return jobExecutionService.getRecentJobExecutions(jobId, limit);
	}
}