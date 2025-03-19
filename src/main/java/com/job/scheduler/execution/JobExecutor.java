package com.job.scheduler.execution;

import com.job.scheduler.model.Job;

import reactor.core.publisher.Mono;

public interface JobExecutor {
	Mono<String> execute(Job job);

	boolean canExecute(Job job);
}
