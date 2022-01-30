package com.thierno.dropwizard.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;

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
@EqualsAndHashCode(exclude = { "actors" })
@Embeddable
@Builder
public class Movie {

	@Id
	@SequenceGenerator(name = "message_pk_sequence", sequenceName = "message_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	String title;
	LocalDate releaseDate;

	@ManyToMany(mappedBy = "movies")
	@Builder.Default
	@JsonIgnore
	Set<Person> actors = new HashSet<>();

	@Tolerate
	public Movie() {
	}
}
