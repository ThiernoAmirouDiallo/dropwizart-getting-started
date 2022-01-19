package com.thierno.dropwizard.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Entity
@Data
@Builder
public class Country {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	String code;

	String name;

	@Tolerate
	public Country() {
	}

}
