package com.example.demo.config.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

@Configuration
public class JpaPagingBatchConfig extends BaseConfig {

	//Jpaで必要
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	//JpaPagibgItemReader
	@Bean
	@StepScope
	public JpaPagingItemReader<Employee> jpaPagingReader(){
		
		String SQL = "select * from employee where gender = :genderParam order by id";
		
		//クエリー設定
		JpaNativeQueryProvider<Employee> queryProvider = 
				new JpaNativeQueryProvider<>();
		queryProvider.setSqlQuery(SQL);
		queryProvider.setEntityClass(Employee.class);
		
		//クエリーに渡すパラメーター
		Map<String, Object> parameterValue = new HashMap<>();
		parameterValue.put("genderParam", 1);
		
		return new JpaPagingItemReaderBuilder<Employee>()
				.entityManagerFactory(entityManagerFactory)
				.name("jpaPagingItemReader")
				.queryProvider(queryProvider)
				.parameterValues(parameterValue)
				.pageSize(5)
				.build();
		
	}
	
	//Stepの生成
	@Bean
	public Step exportJpaPagingStep() throws Exception{
		return this.stepBuilderFactory.get("ExportJpaPagingStep")
				.<Employee, Employee>chunk(10)
				.reader(jpaPagingReader()).listener(readListener)
				.processor(this.genderConvertProcessor)
				.writer(csvWriter()).listener(writeListener)
				.build();
	}
	
	@Bean("JpaPagingJob")
	public Job exportJpaPagingJob() throws Exception{
		return this.jobBuilderFactory.get("ExportJpaPagingJob")
				.incrementer(new RunIdIncrementer())
				.start(exportJpaPagingStep())
				.build();
	}
}
