package com.job.scheduler.execution.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.scheduler.execution.JobExecutor;
import com.job.scheduler.model.Job;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class HttpRequestJobExecutor implements JobExecutor {
	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	public HttpRequestJobExecutor(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
		this.webClient = webClientBuilder.build();
		this.objectMapper = objectMapper;
	}

	@Override
	public Mono<String> execute(Job job) {
		try {
			JsonNode data = objectMapper.readTree(job.getData());
			String url = data.get("url").asText();
			String method = data.get("method").asText("GET");
			String body = data.has("body") ? data.get("body").toString() : null;
			log.info("Executing HTTP request job: {} - {} {}", job.getId(), method, url);
			WebClient.RequestHeadersSpec<?> request;
			switch (method.toUpperCase()) {
			case "GET":
				request = webClient.get().uri(url);
				break;
			case "POST":
				request = webClient.post().uri(url).bodyValue(body != null ? body : "");
				break;
			case "PUT":
				request = webClient.put().uri(url).bodyValue(body != null ? body : "");
				break;
			case "DELETE":
				request = webClient.delete().uri(url);
				break;
			default:
				return Mono.error(new IllegalArgumentException("Unsupported HTTP method: " + method));
			}
			return request.retrieve().bodyToMono(String.class)
					.map(response -> "Request completed successfully: "
							+ response.substring(0, Math.min(100, response.length())) + "...")
					.onErrorResume(e -> Mono.just("Request failed: " + e.getMessage()));
		} catch (Exception e) {
			return Mono.error(new RuntimeException("Failed to parse job data", e));
		}
	}

	@Override
	public boolean canExecute(Job job) {
		return "HTTP_REQUEST".equals(job.getJobClass());
	}
}
