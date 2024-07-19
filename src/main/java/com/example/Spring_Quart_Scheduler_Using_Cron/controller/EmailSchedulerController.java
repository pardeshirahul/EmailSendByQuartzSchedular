package com.example.Spring_Quart_Scheduler_Using_Cron.controller;

import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailRequest;
import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailResponse;
import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailUpdateRequest;
import com.example.Spring_Quart_Scheduler_Using_Cron.service.EmailSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class EmailSchedulerController {

  @Autowired
  private Scheduler scheduler;

  @Autowired
  private EmailSchedulerService emailSchedulerService;

  @PostMapping(value = "/schedule/email")
  public ResponseEntity<Object> scheduleNewJob(@RequestBody EmailRequest emailRequest) {
    EmailResponse emailResponse = emailSchedulerService.scheduleEmail(emailRequest);
    if (emailResponse.isSuccess()) {
      return ResponseEntity.ok()
          .body(emailResponse);
    } else {
      return ResponseEntity.badRequest()
          .body(emailResponse);
    }
  }

  @PostMapping(value = "/update/schedule/email")
  public ResponseEntity<Object> updateSchedulerJob(
      @RequestBody EmailUpdateRequest emailUpdateRequest) {
    EmailResponse emailResponse = emailSchedulerService.reScheduleEmail(emailUpdateRequest);
    if (emailResponse.isSuccess()) {
      return ResponseEntity.ok()
          .body(emailResponse);
    } else {
      return ResponseEntity.badRequest()
          .body(emailResponse);
    }

  }

  @PostMapping(value = "/pause/schedule/email")
  public ResponseEntity<Object> pauseScheduleJob(@RequestParam(value = "jobId", required = true) String jobId) {
    EmailResponse emailResponse = emailSchedulerService.pauseScheduler(jobId);
    if (emailResponse.isSuccess()) {
      return ResponseEntity.ok()
          .body(emailResponse);
    } else {
      return ResponseEntity.badRequest()
          .body(emailResponse);
    }
  }

  @PostMapping(value = "/resume/schedule/email")
  public ResponseEntity<Object> resumeScheduleJob(@RequestParam(value = "jobId", required = true) String jobId) {
    EmailResponse emailResponse = emailSchedulerService.resumeScheduler(jobId);
    if (emailResponse.isSuccess()) {
      return ResponseEntity.ok()
          .body(emailResponse);
    } else {
      return ResponseEntity.badRequest()
          .body(emailResponse);
    }
  }

  @DeleteMapping(value = "/delete/schedule/email")
  public ResponseEntity<Object> deleteScheduleJob(@RequestParam(value = "jobId", required = true) String jobId) {
    EmailResponse emailResponse = emailSchedulerService.deleteScheduler(jobId);
    if (emailResponse.isSuccess()) {
      return ResponseEntity.ok()
          .body(emailResponse);
    } else {
      return ResponseEntity.badRequest()
          .body(emailResponse);
    }
  }

  /* This api use to send simple mail handle exception using concept of Spring Retry
    @retryable and recover by @recover annotation and use simple scheduler at time of recover
   */


  @PostMapping(value = "/send/email")
  public ResponseEntity<Object> sendMailConceptSpringRetry(@RequestBody EmailRequest emailRequest) {
    EmailResponse emailResponse = emailSchedulerService.sendSimpleMail(emailRequest);
    if (emailResponse.isSuccess()) {
      return ResponseEntity.ok()
          .body(emailResponse);
    } else {
      return ResponseEntity.badRequest()
          .body(emailResponse);
    }
  }
}
