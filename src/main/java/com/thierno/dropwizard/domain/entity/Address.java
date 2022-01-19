package com.thierno.dropwizard.domain.entity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Embeddable
@Builder
public class Address {

	String street;
	String city;
	String postalCode;
}