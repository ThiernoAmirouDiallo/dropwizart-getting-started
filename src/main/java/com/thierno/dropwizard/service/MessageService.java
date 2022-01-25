package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Person;

import java.util.List;

public interface MessageService {

	void saveMessage( String message );

	List<Person> testHibernate() throws JsonProcessingException;
}
