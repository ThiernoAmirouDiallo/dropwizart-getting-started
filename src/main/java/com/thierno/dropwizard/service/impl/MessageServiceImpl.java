package com.thierno.dropwizard.service.impl;

import com.thierno.dropwizard.db.util.HibernateEntityManagerFactoryUtil;
import com.thierno.dropwizard.db.util.HibernateSessionFactoryUtil;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.service.MessageService;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageServiceImpl implements MessageService {

	Logger logger = LoggerFactory.getLogger( MessageServiceImpl.class );
	public static boolean USE_JPA = false;

	@Override
	public void saveMessage( String messageValue ) {
		if ( USE_JPA ) {
			saveMessageJpa( messageValue );
		} else {
			saveMessageSession( messageValue );
		}
	}

	private void saveMessageSession( String messageValue ) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Message message = new Message();
		message.setMessage( messageValue );
		session.save( message );
		session.getTransaction().commit();
		session.close();
		logger.info( "Using Hibernate Session - logging using logback" );
	}

	private void saveMessageJpa( String messageValue ) {
		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();

		entityManager.getTransaction().begin();

		Message message = new Message();
		message.setMessage( messageValue );
		entityManager.persist( message );

		entityManager.getTransaction().commit();

		entityManager.close();
		logger.info( "Using JPA EntityManager - logging using logback" );
	}
}
