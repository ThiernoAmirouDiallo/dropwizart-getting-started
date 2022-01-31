package com.thierno.dropwizard.service.impl;

import com.thierno.dropwizard.db.util.HibernateEntityManagerFactoryUtil;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.domain.entity.Parent;
import com.thierno.dropwizard.domain.entity.Person;
import com.thierno.dropwizard.service.JpaService;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaServiceImpl implements JpaService {

	Logger logger = LoggerFactory.getLogger( JpaServiceImpl.class );

	public static boolean USE_JPA = false;

	@Override
	public List<Parent> testJpa() {
		return jpqlAndHqlTest();
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

	private List<Parent> jpqlAndHqlTest() {
		new HibernateServiceImpl().manyToOneAndEmbededIdTest();

		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();
		entityManager.getTransaction().begin();

		List<String> names = entityManager.createQuery( "select parent.id.lastName from Parent as parent" ).getResultList();
		List<Object[]> firstAndLastNames = entityManager.createQuery( "select parent.id.firstName, parent.id.lastName from Parent as parent" ).getResultList();

		//Query query = entityManager.createQuery("select parent from Parent as parent where parent.id.lastName = 'Diallo'");
		//Query query = entityManager.createQuery("select parent from Parent as parent where parent.id.lastName = 'Diallo'");
		Query query = entityManager.createQuery( "select parent from Parent as parent where parent.id.lastName = :lastName" ) //
				.setParameter( "lastName", "Diallo" );
		List<Parent> parents = query.getResultList();

		Parent parent1 = (Parent) entityManager.createQuery( "select parent from Parent as parent where parent.id.firstName = :firstName AND parent.id.lastName = :lastName" ) //
				.setParameter( "firstName", firstAndLastNames.get( 0 )[0] ) //
				.setParameter( "lastName", firstAndLastNames.get( 0 )[1] ) //
				.getSingleResult();

		Parent parents2 = (Parent) entityManager.createNativeQuery( "select * from Parent as parent where parent.lastName = ?1", Parent.class ) //
				.setParameter( 1, "Diallo" ) //
				.getSingleResult();

		Parent parents3 = (Parent) entityManager.createNamedQuery( "findParentByLastName",
						Parent.class ) // named queries defined by Constants.NAMED_QUERIES_MAP and in the Parent class level annotation @NamedQueries
				.setParameter( "lastName", "Diallo" ) //
				.getSingleResult();

		entityManager.getTransaction().commit();
		entityManager.close();

		return parents;
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
