package com.example.Spring_Quart_Scheduler_Using_Cron.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EmailUpdateRequest {
  private String jobId;
  private String jobGroup;
  private String cronExpression;
}
