package com.thierno.dropwizard.service.impl;

import com.thierno.dropwizard.db.util.HibernateEntityManagerFactoryUtil;
import com.thierno.dropwizard.db.util.HibernateSessionFactoryUtil;
import com.thierno.dropwizard.domain.entity.Child;
import com.thierno.dropwizard.domain.entity.Country;
import com.thierno.dropwizard.domain.entity.Department;
import com.thierno.dropwizard.domain.entity.Employee;
import com.thierno.dropwizard.domain.entity.EmployeeCompositeId;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.domain.entity.Movie;
import com.thierno.dropwizard.domain.entity.Parent;
import com.thierno.dropwizard.domain.entity.ParentCompositeId;
import com.thierno.dropwizard.domain.entity.Passport;
import com.thierno.dropwizard.domain.entity.Person;
import com.thierno.dropwizard.model.Sexe;
import com.thierno.dropwizard.service.HibernateService;
import com.thierno.dropwizard.service.JpaService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaServiceImpl implements JpaService {

	Logger logger = LoggerFactory.getLogger( JpaServiceImpl.class );

	public static boolean USE_JPA = false;

	@Override
	public Message testJpa() {
		return saveMessageJpa();
	}

	private Message saveMessageJpa() {
		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();

		entityManager.getTransaction().begin();

		Message message = new Message();
		message.setMessage( "Test message jpa" );
		entityManager.persist( message );

		entityManager.getTransaction().commit();

		entityManager.close();
		logger.info( "Using JPA EntityManager - logging using logback" );

		return message;
	}
}
