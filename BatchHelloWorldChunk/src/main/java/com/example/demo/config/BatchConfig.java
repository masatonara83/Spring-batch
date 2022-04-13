package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	//JobBuilderのfactoryクラス
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	//StepBuilderのfactoryクラス
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	//HelloReader
	@Autowired
	private ItemReader<String> reader;
	
	//HelloProcessor
	@Autowired
	private ItemProcessor<String, String> processor;
	
	//HelloWriter
	@Autowired
	private ItemWriter<String> writer;
	
	@Autowired
	private JobExecutionListener jobExecutionListener;
	
	@Autowired
	private StepExecutionListener stepExecutionListener;
	
	/*ChunkのStepの生成*/
	@Bean
	public Step chunkStep() {
		return stepBuilderFactory.get("HelloChunkStep") //Builderの取得
				.<String, String>chunk(1)	//チャンクの設定
				.reader(reader)	//readerセット
				.processor(processor)	//processorセット
				.writer(writer)	//writerセット
				.listener(stepExecutionListener) //StepListener
				.build();	//stepの生成
	}
	
	/*Jobを生成*/
	@Bean
	public Job chunkJob() throws Exception{
		return jobBuilderFactory.get("HelloWorldChunkJob") //Builderの取得
				.incrementer(new RunIdIncrementer())	//IDのインクリメント
				.start(chunkStep())	//最初のStep
				.listener(jobExecutionListener) //JobListener
				.build(); //Jobの生成
	}
}
