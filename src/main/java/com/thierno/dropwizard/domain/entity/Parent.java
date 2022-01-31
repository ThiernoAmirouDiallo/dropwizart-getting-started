package com.thierno.dropwizard.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
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
@EqualsAndHashCode(exclude = "children")
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Parent.class)
@NamedQueries({
		//@NamedQuery(name = "findParentByLastName", query = "select parent from Parent as parent where parent.id.lastName = :lastName")
})
public class Parent {

	@EmbeddedId
	private ParentCompositeId id;

	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
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
