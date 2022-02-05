package com.thierno.dropwizard.domain.entity.inhetancemapping;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

@Entity
@DiscriminatorValue(AnimalDiscriminator.CAT)
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Cat.class)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Cat extends Animal {

	@Transient
	@Builder.Default
	private String noise = "meow meow";

	@Override
	public String makeNoise() {
		return noise;
	}

	@Tolerate
	public Cat() {
	}
}
