package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Message;

public interface HibernateService {

	void saveMessage( String message );

	Message testHibernate() throws JsonProcessingException;
}
