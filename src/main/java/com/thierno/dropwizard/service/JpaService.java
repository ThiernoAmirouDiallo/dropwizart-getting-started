package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Message;

public interface JpaService {

	Message testJpa() throws JsonProcessingException;
}
