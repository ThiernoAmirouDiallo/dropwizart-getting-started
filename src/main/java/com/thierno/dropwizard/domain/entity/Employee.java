package com.thierno.dropwizard.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Employee.class)
public class Employee {

	@EmbeddedId
	private EmployeeCompositeId id;

	private String name;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "departement_id_fk")
	@MapsId("departementId") //column UserCompositeId.departementId (renamed department_id_cpk_col2) won't be created ; hibernate will use the foreign key
	private Department department;

	@Tolerate
	public Employee() {
	}
}
