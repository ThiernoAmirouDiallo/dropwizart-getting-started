package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Person;

public interface MessageService {

	void saveMessage( String message );

	Person testHibernate() throws JsonProcessingException;
}
