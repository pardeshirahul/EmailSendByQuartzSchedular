package com.example.Spring_Quart_Scheduler_Using_Cron.service.impl;

import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailRequest;
import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailResponse;
import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailUpdateRequest;
import com.example.Spring_Quart_Scheduler_Using_Cron.entity.EmailSchedulerJobInfo;
import com.example.Spring_Quart_Scheduler_Using_Cron.job.EmailJob;
import com.example.Spring_Quart_Scheduler_Using_Cron.repository.EmailRepository;
import com.example.Spring_Quart_Scheduler_Using_Cron.service.EmailSchedulerService;
import jakarta.mail.MessagingException;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.SchedulerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class EmailSchedulerServiceImpl implements EmailSchedulerService {

  @Autowired
  private Scheduler scheduler;

  @Autowired
  private EmailRepository emailRepository;

  @Autowired
  private SchedulerFactoryBean schedulerFactoryBean;

  @Autowired
  private ApplicationContext context;

  @Autowired
  private EmailJob emailJob;

  @Autowired
  private TaskScheduler taskScheduler;

  public EmailSchedulerServiceImpl() {
  }

  @Override
  public EmailResponse scheduleEmail(EmailRequest emailRequest) {
    EmailResponse emailResponse = new EmailResponse();
    try{
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      JobDetail jobDetail = buildJobDetail(emailRequest);
      Trigger trigger =  buildTrigger(jobDetail,emailRequest.getCronExpression());
      scheduler.scheduleJob(jobDetail,trigger);
       emailResponse = new EmailResponse(true, jobDetail.getKey().getName(),
          jobDetail.getKey().getGroup(), "Email Schedule Successfully");

      EmailSchedulerJobInfo emailSchedulerJobInfo =EmailSchedulerJobInfo.builder()
          .jobName(jobDetail.getKey().getName())
          .jobClass(EmailJob.class.getName())
          .jobStatus("SCHEDULED")
          .cronExpression(emailRequest.getCronExpression())
          .jobGroup(jobDetail.getKey().getGroup())
          .build();
      emailRepository.save(emailSchedulerJobInfo);
      log.info(">>>>> jobName = [" +jobDetail.getKey().getName() + "]" + " created.");
      return emailResponse;
    }
    catch (SchedulerException se){
      log.error("Error while scheduling email: ",se);
       emailResponse = new EmailResponse(false,
          "Error while scheduling email. Please try again later!");
      return emailResponse;
    }
  }

  @Override
  public EmailResponse reScheduleEmail(EmailUpdateRequest emailUpdateRequest) {
    EmailResponse emailResponse = new EmailResponse();
    try {
      TriggerKey triggerKey = TriggerKey.triggerKey(emailUpdateRequest.getJobId(), "email-triggers");
      Trigger oldTrigger = scheduler.getTrigger(triggerKey);
      if (oldTrigger == null) {
         emailResponse = new EmailResponse(false, "Job not found");
         return emailResponse;
      }

      CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
      cronTriggerFactoryBean.setName(triggerKey.getName());
      cronTriggerFactoryBean.setGroup(triggerKey.getGroup());
      cronTriggerFactoryBean.setDescription("Rescheduled Email Trigger");
      cronTriggerFactoryBean.setCronExpression(emailUpdateRequest.getCronExpression());
      cronTriggerFactoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
      cronTriggerFactoryBean.afterPropertiesSet();

      scheduler.rescheduleJob(triggerKey, cronTriggerFactoryBean.getObject());

       emailResponse = new EmailResponse(true, "Email Rescheduled Successfully");

       EmailSchedulerJobInfo emailSchedulerJobInfo = emailRepository.findByJobName(
           emailUpdateRequest.getJobId());
       if(ObjectUtils.isEmpty(emailSchedulerJobInfo)){
         emailResponse = new EmailResponse(false, "No any email scheduler job Info Present...");
       }
      emailSchedulerJobInfo.setCronExpression(emailUpdateRequest.getCronExpression());
      emailRepository.save(emailSchedulerJobInfo);
      log.info(">>>>> jobName = [" +triggerKey.getName() + "]" + " rescheduled.");
       return emailResponse;
    } catch (SchedulerException | ParseException se) {
      log.error("Error while rescheduling email: ", se);
       emailResponse = new EmailResponse(false, "Error while rescheduling email. Please try again later!");
      return emailResponse;
    }
  }

  @Override
  public EmailResponse pauseScheduler(String jobId) {
    EmailResponse emailResponse = new EmailResponse();
    TriggerKey triggerKey = TriggerKey.triggerKey(jobId, "email-triggers");
    try {
      Trigger oldTrigger = scheduler.getTrigger(triggerKey);
      if (oldTrigger == null) {
        emailResponse = new EmailResponse(false, "Job not found");
        return emailResponse;
      }
      EmailSchedulerJobInfo getJobInfo = emailRepository.findByJobName(jobId);
      getJobInfo.setJobStatus("PAUSED");
      emailRepository.save(getJobInfo);
      schedulerFactoryBean.getScheduler().pauseJob(JobKey.jobKey(jobId, "email-jobs"));
      log.info(">>>>> jobName = [" + triggerKey.getName() + "]" + " paused.");
      emailResponse = new EmailResponse(true, "Email Paused Successfully");
      return emailResponse;
    } catch (SchedulerException e) {
      log.error("Failed to pause job - {}", triggerKey.getName(), e);
      emailResponse = new EmailResponse(false, "Error while pausing email. Please try again later!");
      return emailResponse;
    }
  }

  @Override
  public EmailResponse resumeScheduler(String jobId) {
    EmailResponse emailResponse = new EmailResponse();
    TriggerKey triggerKey = TriggerKey.triggerKey(jobId, "email-triggers");
    try {
      Trigger oldTrigger = scheduler.getTrigger(triggerKey);
      if (oldTrigger == null) {
        emailResponse = new EmailResponse(false, "Job not found");
        return emailResponse;
      }
      EmailSchedulerJobInfo getJobInfo = emailRepository.findByJobName(jobId);
      getJobInfo.setJobStatus("RESUME");
      emailRepository.save(getJobInfo);
      schedulerFactoryBean.getScheduler().resumeJob(JobKey.jobKey(jobId, "email-jobs"));
      log.info(">>>>> jobName = [" + triggerKey.getName() + "]" + " resumed.");
      emailResponse = new EmailResponse(true, "Email Resumed Successfully");
      return emailResponse;
    } catch (SchedulerException e) {
      log.error("Failed to resume job - {}", triggerKey.getName(), e);
      emailResponse = new EmailResponse(false, "Error while resume email. Please try again later!");
      return emailResponse;
    }
  }

  @Override
  public EmailResponse deleteScheduler(String jobId) {
    EmailResponse emailResponse = new EmailResponse();
    TriggerKey triggerKey = TriggerKey.triggerKey(jobId, "email-triggers");
    try {
      Trigger oldTrigger = scheduler.getTrigger(triggerKey);
      if (oldTrigger == null) {
        emailResponse = new EmailResponse(false, "Job not found");
        return emailResponse;
      }
      EmailSchedulerJobInfo getJobInfo = emailRepository.findByJobName(jobId);
      emailRepository.delete(getJobInfo);
      schedulerFactoryBean.getScheduler().deleteJob(JobKey.jobKey(jobId, "email-jobs"));
      log.info(">>>>> jobName = [" + triggerKey.getName() + "]" + " deleted.");
      emailResponse = new EmailResponse(true, "Email deleted Successfully");
      return emailResponse;
    } catch (SchedulerException e) {
      log.error("Failed to delete job - {}", triggerKey.getName(), e);
      emailResponse = new EmailResponse(false, "Error while delete email. Please try again later!");
      return emailResponse;
    }
  }

  @Override
  @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
  public EmailResponse sendSimpleMail(EmailRequest emailRequest) {
    System.out.println("-------------");
    emailJob.sendMail(emailRequest.getEmail(),emailRequest.getSubject(),emailRequest.getBody());
    return new EmailResponse(true,"Email send Successfully");
  }


  /*
   * @Retryable use for here purpose is this method is try for 3(maxAttempts) time to call if
   * any Exception occur like call once and any exception
   */

  /*
   * Delay use for like if first time exception then wait for delay time and try
   * again to call this method like delay = 2000 means if fails then after 2
   * second this method call again
   */

  /*
   * Mutliplier use for like intial dealy * multiplier
   * first time instance call if delay is 1000(1 sec)
   * second time delay * 5 (after 5 second)
   * third time delay (5*2) (after 10 second)
   */
  @Recover
  public EmailResponse recover(EmailRequest emailRequest){
    emailRequest.setCronExpression("0 12 19 * * ?");
    return scheduleEmail(emailRequest);
  }

  private JobDetail buildJobDetail(EmailRequest emailRequest){
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(EmailJob.class);
    factoryBean.setName(UUID.randomUUID().toString());
    factoryBean.setGroup("email-jobs");
    factoryBean.setDescription("Send Email Job");

    // Set JobDataMap
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("email", emailRequest.getEmail());
    jobDataMap.put("subject", emailRequest.getSubject());
    jobDataMap.put("body", emailRequest.getBody());
    jobDataMap.put("croExpression", emailRequest.getCronExpression());
    factoryBean.setJobDataMap(jobDataMap);

    factoryBean.setDurability(true);
    factoryBean.setApplicationContext(context);
    factoryBean.afterPropertiesSet(); // Initialize the factory bean

    return factoryBean.getObject();

  }

  private Trigger buildTrigger(JobDetail jobDetail, String cronExpression){
    CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
    factoryBean.setJobDetail(jobDetail);
    factoryBean.setCronExpression(cronExpression);
    factoryBean.setName(jobDetail.getKey().getName());
    factoryBean.setGroup("email-triggers");
    factoryBean.setDescription("Send Email Trigger");
    factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
    try {
      factoryBean.afterPropertiesSet();
    } catch (ParseException e) {
      log.error("Error setting CronTrigger properties: {}", e.getMessage(), e);
    }
    return factoryBean.getObject();
  }
}
