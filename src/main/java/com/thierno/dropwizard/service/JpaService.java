package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Guide;

public interface JpaService {

	Guide testJpa() throws JsonProcessingException;
}
