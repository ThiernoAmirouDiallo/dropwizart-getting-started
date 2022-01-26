package com.thierno.dropwizard.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Department.class)
public class Department {

	@Id
	@SequenceGenerator(name = "department_pk_sequence", sequenceName = "department_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	String name;

	@OneToMany(mappedBy = "department")
	@Builder.Default
	private Set<Employee> users = new HashSet<>();

	@Tolerate
	public Department() {
	}

	public void addUser( Employee user ) {
		users.add( user );
		user.setDepartment( this );
	}
}
