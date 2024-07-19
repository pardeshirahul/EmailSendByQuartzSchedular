package com.example.Spring_Quart_Scheduler_Using_Cron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//This below two annotation use for simple scheduling and used Spring retry concept only not for quartz scheduler
@EnableRetry
@EnableScheduling
public class SpringQuartSchedulerUsingCronApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringQuartSchedulerUsingCronApplication.class, args);
	}

}
