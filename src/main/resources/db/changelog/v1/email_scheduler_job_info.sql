--liquibase formatted sql
--changeset spring_quartz_scheduler_sing_cron:0000

CREATE TABLE IF NOT EXISTS email_scheduler_job_info (
    job_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(255) NOT NULL,
    job_group VARCHAR(255) NOT NULL,
    job_status VARCHAR(255) NOT NULL,
    job_class VARCHAR(255) NOT NULL,
    cron_expression VARCHAR(255) NOT NULL
);