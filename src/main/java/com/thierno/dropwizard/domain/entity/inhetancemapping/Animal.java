package com.thierno.dropwizard.domain.entity.inhetancemapping;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // won't work with @MappedSuperclass
@DiscriminatorColumn(discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue(AnimalDiscriminator.DEFAULT)
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Animal.class)
@SuperBuilder
public abstract class Animal {

	public static final String DEFAULT_DISCRIMINAOTOR = "-";

	@Id
	@SequenceGenerator(name = "animal_pk_sequence", sequenceName = "animal_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "animal_pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	private String name;

	public abstract String makeNoise();

	@Tolerate
	public Animal() {
	}
}
