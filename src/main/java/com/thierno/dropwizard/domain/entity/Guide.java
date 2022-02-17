package com.thierno.dropwizard.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Entity
@Getter
@Setter
@ToString(exclude = "students")
@EqualsAndHashCode(exclude = "students")
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Guide.class)
@NamedQueries({
		//@NamedQuery(name = "findGuideByLastName", query = "select guide from Guide as guide where guide.id.lastName = :lastName")
})
@BatchSize(size = 3) // to improve N + 1 SELECTS problem when fecth Student.guide lazily
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Guide {

	@Id
	@SequenceGenerator(name = "guide_pk_sequence", sequenceName = "guide_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guide_pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Embedded
	private CompositeName name;

	@OneToMany(mappedBy = "guide")
	@Builder.Default
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Student> students = new HashSet<>();

	@Tolerate
	public Guide() {
	}

	public void addStudent( Student student ) {
		students.add( student );
		student.setGuide( this );
	}
}
