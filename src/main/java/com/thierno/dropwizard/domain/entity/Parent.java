package com.thierno.dropwizard.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Parent.class)
public class Parent {

	@EmbeddedId
	private ParentCompositeId id;

	@OneToMany(mappedBy = "parent")
	@Builder.Default
	private Set<Child> children = new HashSet<>();

	@Tolerate
	public Parent() {
	}

	public void addChild( Child child ) {
		children.add( child );
		child.setParent( this );
	}
}
