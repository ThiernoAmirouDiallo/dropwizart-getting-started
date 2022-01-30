package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Person;

public interface JpaService {

	Person testJpa() throws JsonProcessingException;
}
