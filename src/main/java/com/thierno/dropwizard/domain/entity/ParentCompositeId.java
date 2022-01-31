package com.thierno.dropwizard.domain.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

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
@Embeddable
@Builder
public class ParentCompositeId implements Serializable {

	private static final long serialVersionUID = 5858083517781630155L;
	private String firstName;
	private String lastName;

	@Tolerate
	public ParentCompositeId() {
	}
}
