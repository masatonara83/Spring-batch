package com.example.demo.listener;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

import com.example.demo.domain.model.Employee;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessListner implements ItemProcessListener<Employee, Employee> {
	
	@Override
	public void beforeProcess(Employee item) {
		//何もしない
		
	}

	@Override
	public void afterProcess(Employee item, Employee result) {
		//何もしない
		
	}

	@Override
	public void onProcessError(Employee item, Exception e) {
		log.error("ProcessorError: item={}, errorMessage={}", item, e.getMessage(), e );
		
	}

	
}
