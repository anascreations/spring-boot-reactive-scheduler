package com.job.scheduler.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.job.scheduler.dto.JobDto;
import com.job.scheduler.model.Job;
import com.job.scheduler.repository.JobRepository;
import com.job.scheduler.util.CronCalculator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class JobService {
	private final JobRepository jobRepository;
	private final CronCalculator cronCalculator;

	public Flux<Job> getAllJobs() {
		return jobRepository.findAll();
	}

	public Mono<Job> getJobById(Long id) {
		return jobRepository.findById(id);
	}

	public Mono<Job> createJob(JobDto jobDTO) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextRunAt = cronCalculator.getNextExecutionTime(jobDTO.getCronExpression(), now);
		Job job = Job.builder().name(jobDTO.getName()).cronExpression(jobDTO.getCronExpression())
				.jobClass(jobDTO.getJobClass()).data(jobDTO.getData()).status(Job.JobStatus.SCHEDULED).createdAt(now)
				.updatedAt(now).nextRunAt(nextRunAt).build();
		return jobRepository.save(job);
	}

	public Mono<Job> updateJob(Long id, JobDto jobDTO) {
		return jobRepository.findById(id).flatMap(existingJob -> {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime nextRunAt;
			if (!existingJob.getCronExpression().equals(jobDTO.getCronExpression())) {
				nextRunAt = cronCalculator.getNextExecutionTime(jobDTO.getCronExpression(), now);
			} else {
				nextRunAt = existingJob.getNextRunAt();
			}
			existingJob.setName(jobDTO.getName());
			existingJob.setCronExpression(jobDTO.getCronExpression());
			existingJob.setJobClass(jobDTO.getJobClass());
			existingJob.setData(jobDTO.getData());
			existingJob.setUpdatedAt(now);
			existingJob.setNextRunAt(nextRunAt);
			return jobRepository.save(existingJob);
		});
	}

	public Mono<Job> pauseJob(Long id) {
		return updateJobStatus(id, Job.JobStatus.PAUSED);
	}

	public Mono<Job> resumeJob(Long id) {
		return jobRepository.findById(id).flatMap(job -> {
			if (job.getStatus() != Job.JobStatus.PAUSED) {
				return Mono.just(job);
			}
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime nextRunAt = cronCalculator.getNextExecutionTime(job.getCronExpression(), now);
			job.setStatus(Job.JobStatus.SCHEDULED);
			job.setNextRunAt(nextRunAt);
			job.setUpdatedAt(now);
			return jobRepository.save(job);
		});
	}

	public Mono<Job> cancelJob(Long id) {
		return updateJobStatus(id, Job.JobStatus.CANCELLED);
	}

	public Mono<Void> deleteJob(Long id) {
		return jobRepository.deleteById(id);
	}

	public Flux<Job> findJobsDueForExecution(LocalDateTime now) {
		return jobRepository.findJobsDueForExecution(now);
	}

	public Mono<Job> markJobAsRunning(Long id) {
		return updateJobStatus(id, Job.JobStatus.RUNNING);
	}

	public Mono<Job> completeJob(Long id) {
		LocalDateTime now = LocalDateTime.now();
		return jobRepository.findById(id).flatMap(job -> {
			LocalDateTime nextRunAt = cronCalculator.getNextExecutionTime(job.getCronExpression(), now);
			return jobRepository.updateJobStatus(id, Job.JobStatus.SCHEDULED, now, nextRunAt, now)
					.then(jobRepository.findById(id));
		});
	}

	public Mono<Job> failJob(Long id) {
		LocalDateTime now = LocalDateTime.now();
		return jobRepository.findById(id).flatMap(job -> {
			LocalDateTime nextRunAt = cronCalculator.getNextExecutionTime(job.getCronExpression(), now);
			return jobRepository.updateJobStatus(id, Job.JobStatus.SCHEDULED, now, nextRunAt, now)
					.then(jobRepository.findById(id));
		});
	}

	private Mono<Job> updateJobStatus(Long id, Job.JobStatus status) {
		return jobRepository.findById(id).flatMap(job -> {
			LocalDateTime now = LocalDateTime.now();
			job.setStatus(status);
			job.setUpdatedAt(now);
			return jobRepository.save(job);
		});
	}
}
