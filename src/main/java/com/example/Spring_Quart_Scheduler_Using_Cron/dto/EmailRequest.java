package com.example.Spring_Quart_Scheduler_Using_Cron.dto;

import lombok.Data;

@Data
public class EmailRequest {


  private String email;

  private String subject;

  private String body;

  private String cronExpression;
}
