package com.thierno.dropwizard.domain.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Message {

	@Id
	@SequenceGenerator(name = "message_pk_sequence", sequenceName = "message_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	private String message;

	@Tolerate
	public Message() {
	}
}
