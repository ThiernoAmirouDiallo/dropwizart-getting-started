package com.thierno.dropwizard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.domain.entity.Student;

import java.util.List;

public interface JpaService {

	List<Student> testJpa() throws JsonProcessingException;
}
