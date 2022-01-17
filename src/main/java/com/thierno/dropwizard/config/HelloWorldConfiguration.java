package com.thierno.dropwizard.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

import io.dropwizard.Configuration;
import lombok.Data;

@Data
public class HelloWorldConfiguration extends Configuration {

	@NotEmpty
	@JsonProperty
	private String template;

	@JsonProperty
	private Boolean useJpa = false;

	@NotEmpty
	@JsonProperty
	private String defaultName = "Stranger";
}
