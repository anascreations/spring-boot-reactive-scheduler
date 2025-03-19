package com.job.scheduler.execution;

import java.util.List;

import org.springframework.stereotype.Component;

import com.job.scheduler.model.Job;

@Component
public class JobExecutorRegistry {
	private final List<JobExecutor> executors;

	public JobExecutorRegistry(List<JobExecutor> executors) {
		this.executors = executors;
	}

	public JobExecutor getExecutor(Job job) {
		return executors.stream().filter(executor -> executor.canExecute(job)).findFirst().orElseThrow(
				() -> new IllegalArgumentException("No executor found for job class: " + job.getJobClass()));
	}
}
