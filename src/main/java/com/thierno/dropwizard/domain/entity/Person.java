package com.thierno.dropwizard.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.thierno.dropwizard.model.Sexe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.UniqueConstraint;

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
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Person.class)
public class Person {

	@Id
	@SequenceGenerator(name = "person_pk_sequence", sequenceName = "person_id_seq", allocationSize = 1)
	//@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_pk_sequence")
	//@Column(name = "id", unique = true, nullable = false)
	private Long id;

	private String firstName;
	private String lastName;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "street", column = @Column(name = "home_street")),
			@AttributeOverride(name = "city", column = @Column(name = "home_city")),
			@AttributeOverride(name = "postalCode", column = @Column(name = "home_postalCode")) })
	private Address homeAddress;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "street", column = @Column(name = "billing_street")),
			@AttributeOverride(name = "city", column = @Column(name = "billing_city")),
			@AttributeOverride(name = "postalCode", column = @Column(name = "billing_postalCode")) })
	private Address billingAddress;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "country_id")
	private Country country;

	@OneToOne(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "passport_id", unique = true)
	@MapsId
	Passport passport;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "movie_person", //
			joinColumns = { @JoinColumn(name = "movie_id") }, //
			inverseJoinColumns = { @JoinColumn(name = "person_id") } //
	)
	@Builder.Default
	Set<Movie> movies = new HashSet<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	Sexe sexe;

	@ElementCollection
	@CollectionTable(name = "person_nickname", //
			joinColumns = { @JoinColumn(name = "person_id") }, //
			uniqueConstraints = { @UniqueConstraint(columnNames = { "person_id", "nickname" }) } //
	)
	@Column(name = "nickname")
	@Builder.Default
	Set<String> nicknames = new HashSet<>();
	//Collection<Address> addresses = new ArrayList<>();

	@Tolerate
	public Person() {
	}
}
