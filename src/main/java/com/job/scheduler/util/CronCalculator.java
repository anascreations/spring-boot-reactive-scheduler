package com.job.scheduler.util;

import java.time.LocalDateTime;

import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

@Component
public class CronCalculator {
	public LocalDateTime getNextExecutionTime(String cronExpression, LocalDateTime baseTime) {
		try {
			CronExpression cron = CronExpression.parse(cronExpression);
			return cron.next(baseTime);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid cron expression: " + cronExpression, e);
		}
	}
}
