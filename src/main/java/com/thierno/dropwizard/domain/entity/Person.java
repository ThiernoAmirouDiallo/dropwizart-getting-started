package com.thierno.dropwizard.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Person {

	@Id
	@SequenceGenerator(name = "person_pk_sequence", sequenceName = "person_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	Long id;

	String firstName;
	String lastName;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "street", column = @Column(name = "home_street")),
			@AttributeOverride(name = "city", column = @Column(name = "home_city")),
			@AttributeOverride(name = "postalCode", column = @Column(name = "home_postalCode")) })
	Address homeAddress;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "street", column = @Column(name = "billing_street")),
			@AttributeOverride(name = "city", column = @Column(name = "billing_city")),
			@AttributeOverride(name = "postalCode", column = @Column(name = "billing_postalCode")) })
	Address billingAddress;
}
