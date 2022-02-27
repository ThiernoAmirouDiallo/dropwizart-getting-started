package com.thierno.dropwizard.domain.entity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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


	@ManyToOne
	@JoinColumn(name = "guide_id", referencedColumnName = "id")
	Guide guide;

	@ManyToOne
	@JoinColumn(name = "creator_id", referencedColumnName = "passport_id") // Person.passport_id is mapped to Person.id using @MapsId
	Person creator;
}
