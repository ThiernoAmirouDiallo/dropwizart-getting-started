package com.thierno.dropwizard.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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
@EqualsAndHashCode
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Child.class)
public class Child {

	@Id
	@SequenceGenerator(name = "child_pk_sequence", sequenceName = "child_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "child_pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	private String name;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumns({
			@JoinColumn(name = "firstname_fk", referencedColumnName = "firstname"), //
			@JoinColumn(name = "lastname_fk", referencedColumnName = "lastname") //
	})
	private Parent parent;

	@Tolerate
	public Child() {
	}
}
