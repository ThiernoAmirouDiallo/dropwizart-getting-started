package com.thierno.dropwizard.domain.entity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Embeddable
@Builder
public class Address {

	private String street;
	private String city;
	private String postalCode;

	@Tolerate
	public Address() {
	}
}
