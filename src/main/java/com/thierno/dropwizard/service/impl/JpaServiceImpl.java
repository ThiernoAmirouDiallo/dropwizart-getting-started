package com.thierno.dropwizard.service.impl;

import com.thierno.dropwizard.db.util.HibernateEntityManagerFactoryUtil;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.domain.entity.Person;
import com.thierno.dropwizard.service.JpaService;

import javax.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaServiceImpl implements JpaService {

	Logger logger = LoggerFactory.getLogger( JpaServiceImpl.class );

	public static boolean USE_JPA = false;

	@Override
	public Person testJpa() {
		return testOrderBy();
	}

	private Person testOrderBy() {
		new HibernateServiceImpl().manyToManyTest();

		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();

		entityManager.getTransaction().begin();

		Person person = entityManager.find( Person.class, 1L );

		entityManager.getTransaction().commit();

		entityManager.close();
		logger.info( "Using JPA EntityManager - logging using logback" );

		return person;
	}

	private Message saveMessageJpa() {
		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();

		entityManager.getTransaction().begin();

		Message message = new Message();
		message.setMessage( "Test message jpa" );
		entityManager.persist( message );

		entityManager.flush(); // dirty check and issue SQL queries if necessary

		entityManager.refresh( message ); // reload from DB
		entityManager.remove( message ); // if hibernate.use_identifier_rollback=true, message.id will be reset to null
		entityManager.persist( message ); // rollback the deletion

		Message foundMessage = entityManager.find( Message.class, message.getId() );

		Message messageProxy = entityManager.getReference( Message.class, foundMessage.getId() ); // loods proxy
		Hibernate.initialize( messageProxy ); // initializes the proxy

		entityManager.getTransaction().commit();

		entityManager.close();
		logger.info( "Using JPA EntityManager - logging using logback" );

		return message;
	}
}
