package com.example.Spring_Quart_Scheduler_Using_Cron.service;

import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailRequest;
import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailResponse;
import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailUpdateRequest;

public interface EmailSchedulerService {

  EmailResponse scheduleEmail(EmailRequest emailRequest);

  EmailResponse reScheduleEmail(EmailUpdateRequest emailUpdateRequest);

  EmailResponse pauseScheduler(String jobId);

  EmailResponse resumeScheduler(String jobId);
  EmailResponse deleteScheduler(String jobId);
  EmailResponse sendSimpleMail(EmailRequest emailRequest);
}
