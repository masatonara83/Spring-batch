package com.example.demo.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.domain.model.Employee;

@EnableBatchProcessing
public abstract class BaseConfig {

	@Autowired
	protected JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	protected StepBuilderFactory stepBuilderFactory;
	
	/**性別の文字列を数値に変化をするProcessor*/
	@Autowired
	@Qualifier("GenderConvertProcessor")
	protected ItemProcessor<Employee, Employee> genderConvertProcessor;
	
	//データの存在をチェックをするProcessor
	@Autowired
	@Qualifier("ExistsCheckProcessor")
	protected ItemProcessor<Employee, Employee> existsCheckProcessor;
	
	/**ReadListener*/
	@Autowired
	protected ItemReadListener<Employee> readListener;
	
	//ProcessorListener
	@Autowired
	protected ItemProcessListener<Employee, Employee> processListener;
	
	/*WriteListener*/
	@Autowired
	protected ItemWriteListener<Employee> writeListener;
	
	@Autowired
	protected SampleProperty property;
	
	//CSVファイルのReader
	@Bean
	@StepScope
	public FlatFileItemReader<Employee> csvReader(){
		//CSVのカラムにつける名前
		String[] nameArray = new String[] {"id", "name", "age", "genderString"};
		
		//ファイルの読み込み設定
		return new FlatFileItemReaderBuilder<Employee>() //Builderの生成
				.name("employeeCsvReader") //名前
				.resource(new ClassPathResource(property.getCsvPath())) //ファイルパス
				.linesToSkip(1) //スキップする行数
				.encoding(StandardCharsets.UTF_8.name()) //文字コード
				.delimited() //DelimitedBuilderを取得
				.names(nameArray) //ファイルのカラムにつける名前
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
					//ファイルの値をクラスにバインド
					setTargetType(Employee.class);
					}
				})
				.build();	//renderの生成
	}
	
	//複数のProcessor
	@Bean
	@StepScope
	public ItemProcessor<Employee, Employee> compositeProcessor(){
		CompositeItemProcessor<Employee, Employee> compositeProcessor = 
				new CompositeItemProcessor<>();
		
		//ProcessorList
		compositeProcessor.setDelegates(Arrays.asList(
				validationProcessor(),
				this.existsCheckProcessor, 
				this.genderConvertProcessor));
		
		return compositeProcessor;
	}
	
	//ValidationのProcessor
	@Bean
	@StepScope
	public BeanValidatingItemProcessor<Employee> validationProcessor(){
		BeanValidatingItemProcessor<Employee> validationProcessor =
				new BeanValidatingItemProcessor<>();
		
		//true:skip
		//false: throw ValidationException
		validationProcessor.setFilter(true);
		
		return validationProcessor;
	}
	
}
