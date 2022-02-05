package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.inhetancemapping.Animal;

import java.util.List;

public interface JpaService {

	List<Animal> testJpa() throws JsonProcessingException;
}
