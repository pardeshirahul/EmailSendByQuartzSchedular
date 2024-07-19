package com.example.Spring_Quart_Scheduler_Using_Cron.job;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


//we use QuartzJobBean because we can autowired another bean in this class so its easy to maintain while
//using job interface we cannot autowired another bean

@Component
@Slf4j
public class EmailJob extends QuartzJobBean {

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private MailProperties mailProperties;
  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    JobDataMap jobDataMap = context.getMergedJobDataMap();

    String subject = jobDataMap.getString("subject");
    String body = jobDataMap.getString("body");
    String recipientEmail = jobDataMap.getString("email");
    log.info(">>>>> SimpleCronJob Start................");
   sendMail(recipientEmail, subject, body);
  }

  public void sendMail(String toEmail, String subject, String body){
    try{
      MimeMessage message =  mailSender.createMimeMessage();

      MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
      messageHelper.setSubject(subject);
      messageHelper.setText(body, true);
      messageHelper.setFrom(mailProperties.getUsername());
      messageHelper.setTo(toEmail);

      mailSender.send(message);
    }catch (MessagingException ex){
      System.out.println(ex);
    }
  }
}

