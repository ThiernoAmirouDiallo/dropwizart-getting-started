package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Child;

import java.util.List;

public interface MessageService {

	void saveMessage( String message );

	List<Child> testHibernate() throws JsonProcessingException;
}
