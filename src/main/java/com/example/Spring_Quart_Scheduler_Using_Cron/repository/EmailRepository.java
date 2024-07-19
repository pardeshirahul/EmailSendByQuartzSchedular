package com.example.Spring_Quart_Scheduler_Using_Cron.repository;

import com.example.Spring_Quart_Scheduler_Using_Cron.entity.EmailSchedulerJobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<EmailSchedulerJobInfo,Long> {
  EmailSchedulerJobInfo findByJobName(String jobName);
}
