package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

	/*StepbuilderのFactoryクラス*/
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	/*JobBuilderのFactoryクラス*/
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private Tasklet retryTasklet;
	
	/*Step作成*/
	@Bean
	public Step retryTaskStep() {
		return stepBuilderFactory.get("RetryTaskletStep")
				.tasklet(retryTasklet).build();
	}
	
	/*Jobの生成*/
	@Bean
	@Scheduled(cron = "${cron.pattern1}")
	public Job retryTaskletJob() throws Exception{
		return jobBuilderFactory.get("retryTaskletjob")
				.incrementer(new RunIdIncrementer())
				.start(retryTaskStep())
				.build();
	}
}
