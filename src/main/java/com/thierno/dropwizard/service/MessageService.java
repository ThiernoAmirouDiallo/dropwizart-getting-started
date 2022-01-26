package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Employee;

import java.util.List;

public interface MessageService {

	void saveMessage( String message );

	List<Employee> testHibernate() throws JsonProcessingException;
}
