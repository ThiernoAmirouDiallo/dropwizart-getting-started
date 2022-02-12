package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Parent;

import java.util.List;

public interface JpaService {

	List<Parent> testJpa() throws JsonProcessingException;
}
