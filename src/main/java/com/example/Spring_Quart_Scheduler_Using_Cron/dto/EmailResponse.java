package com.example.Spring_Quart_Scheduler_Using_Cron.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@Getter
@Setter
public class EmailResponse {

  private boolean success;
  private String jobId;
  private String jobGroup;
  private String message;

  public EmailResponse(boolean success, String jobId, String jobGroup, String message) {
    this.success = success;
    this.jobId = jobId;
    this.jobGroup = jobGroup;
    this.message = message;
  }

  public EmailResponse(boolean success, String message) {
    this.success = success;
    this.message = message;
  }
}
