package com.job.scheduler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.job.scheduler.dto.JobDto;
import com.job.scheduler.model.Job;
import com.job.scheduler.service.JobService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/jobs")
@RequiredArgsConstructor
public class JobController {
	private final JobService jobService;

	@GetMapping
	public Flux<Job> getAllJobs() {
		return jobService.getAllJobs();
	}

	@GetMapping("{id}")
	public Mono<Job> getJobById(@PathVariable Long id) {
		return jobService.getJobById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Job> createJob(@Valid @RequestBody JobDto jobDTO) {
		return jobService.createJob(jobDTO);
	}

	@PutMapping("{id}")
	public Mono<Job> updateJob(@PathVariable Long id, @Valid @RequestBody JobDto jobDTO) {
		return jobService.updateJob(id, jobDTO);
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> deleteJob(@PathVariable Long id) {
		return jobService.deleteJob(id);
	}

	@PostMapping("{id}/pause")
	public Mono<Job> pauseJob(@PathVariable Long id) {
		return jobService.pauseJob(id);
	}

	@PostMapping("{id}/resume")
	public Mono<Job> resumeJob(@PathVariable Long id) {
		return jobService.resumeJob(id);
	}

	@PostMapping("{id}/cancel")
	public Mono<Job> cancelJob(@PathVariable Long id) {
		return jobService.cancelJob(id);
	}
}
