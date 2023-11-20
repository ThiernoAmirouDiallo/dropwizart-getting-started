package com.thierno.dropwizard.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

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

	@JsonProperty
	Map<String, Map<String, String>> keyStoreConfig;
}
