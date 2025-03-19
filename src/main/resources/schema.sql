CREATE TABLE IF NOT EXISTS jobs (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cron_expression VARCHAR(100) NOT NULL,
    job_class VARCHAR(255) NOT NULL,
    data JSONB,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_run_at TIMESTAMP,
    next_run_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS job_executions (
    id SERIAL PRIMARY KEY,
    job_id INTEGER NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    duration_ms BIGINT,
    result TEXT,
    error TEXT
);

CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_next_run_at ON jobs(next_run_at);
CREATE INDEX idx_job_executions_job_id ON job_executions(job_id);