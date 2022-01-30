package com.thierno.dropwizard.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Data
@Builder
@EqualsAndHashCode(exclude = { "people" })
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Country.class)
public class Country {

	@Id
	@SequenceGenerator(name = "country_pk_sequence", sequenceName = "country_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "code", unique = true, nullable = false)
	private String code;

	private String name;

	@JsonIgnore
	@OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
	private Set<Person> people = new HashSet<>();

	@Tolerate
	public Country() {
	}

}
