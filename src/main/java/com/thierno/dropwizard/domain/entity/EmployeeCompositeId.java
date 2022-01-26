package com.thierno.dropwizard.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Embeddable
@Builder
public class EmployeeCompositeId implements Serializable {

	private static final long serialVersionUID = 5858083517781630155L;

	@Column(name = "employee_name_cpk_col1")
	private String employeeName;

	@Column(name = "department_id_cpk_col2")
	private Long departementId;
}
