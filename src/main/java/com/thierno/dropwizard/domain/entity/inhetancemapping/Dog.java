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
@DiscriminatorValue( AnimalDiscriminator.DOG )
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Dog.class)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Dog extends Animal {

	@Transient
	@Builder.Default
	private String noise = "woof woof";

	@Override
	public String makeNoise() {
		return noise;
	}

	@Tolerate
	public Dog() {
	}

}
