package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Employee;
import com.thierno.dropwizard.domain.entity.Message;

import java.util.List;

public interface JpaService {

	Message testJpa() throws JsonProcessingException;
}
