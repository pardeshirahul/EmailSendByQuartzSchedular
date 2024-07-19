package com.example.Spring_Quart_Scheduler_Using_Cron.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailSchedulerJobInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long jobId;
  private String jobName;
  private String jobGroup;
  private String jobStatus;
  private String jobClass;
  private String cronExpression;
}
