package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Student;

import java.util.List;

public interface HibernateService {

	void saveMessage( String message );

	List<Student> testHibernate() throws JsonProcessingException;
}
