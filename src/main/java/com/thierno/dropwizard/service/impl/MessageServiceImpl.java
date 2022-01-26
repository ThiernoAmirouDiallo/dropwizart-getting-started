package com.thierno.dropwizard.service.impl;

import com.thierno.dropwizard.db.util.HibernateEntityManagerFactoryUtil;
import com.thierno.dropwizard.db.util.HibernateSessionFactoryUtil;
import com.thierno.dropwizard.domain.entity.Country;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.domain.entity.Movie;
import com.thierno.dropwizard.domain.entity.Passport;
import com.thierno.dropwizard.domain.entity.Person;
import com.thierno.dropwizard.model.Sexe;
import com.thierno.dropwizard.service.MessageService;

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

	@Override
	public List<Person> testHibernate() {
		return manyToManyTest();
	}

	private List<Person> jpqlTest() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		List<Person> guineans = session.createQuery( "select p from Person p where p.country.code = :country_code" ) //
				.setParameter( "country_code", "GN" ) //
				.getResultList();

		session.getTransaction().commit();
		session.close();

		return guineans;
	}

	private Person oneToManyTest() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Person person = Person.builder().firstName( "Thierno" ).sexe( Sexe.MALE ).lastName( "Diallo" ).country( getCountByCode( "GN" ) ).build();

		session.save( person );

		session.getTransaction().commit();
		session.close();

		return person;
	}

	private Person oneToOneTest() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Person person = Person.builder().firstName( "Thierno" ) //
				.lastName( "Diallo" ) //
				.sexe( Sexe.MALE )
				.country( getCountByCode( "GN" ) ) //
				.passport( Passport.builder().expirationDate( LocalDate.now().plusYears( 5 ).minusDays( 1 ) ).build() ) //
				.build();

		person.getPassport().setPerson( person );

		session.saveOrUpdate( person.getPassport() ); // persist person would save both person and passport.
		session.saveOrUpdate( person );

		session.getTransaction().commit();
		session.close();

		return person;
	}

	private List<Person> manyToManyTest() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Movie movie1 = Movie.builder().title( "Money Hiest" ).build();
		Movie movie2 = Movie.builder().title( "The Last Kingdom" ).build();

		Person person1 = Person.builder().firstName( "Thierno" ) //
				.lastName( "Diallo" ) //
				.sexe( Sexe.MALE )
				.country( getCountByCode( "GN" ) ) //
				.passport( Passport.builder().expirationDate( LocalDate.now().plusYears( 5 ).minusDays( 1 ) ).build() ) //
				.build();
		person1.getPassport().setPerson( person1 );
		person1.getMovies().add( movie1 );
		person1.getMovies().add( movie2 );
		person1.getNicknames().add( "Server Engineer" );
		session.persist( person1 );

		Person person2 = Person.builder().firstName( "Amirou" ) //
				.lastName( "Diallo" ) //
				.sexe( Sexe.FEMALE )
				.country( getCountByCode( "GN" ) ) //
				.passport( Passport.builder().expirationDate( LocalDate.now().plusYears( 5 ).minusDays( 1 ) ).build() ) //
				.build();
		person2.getPassport().setPerson( person2 );
		person2.getMovies().add( movie1 );
		person2.getNicknames().add( "Programmer" );
		person2.getNicknames().add( "SWE" );
		session.persist( person2 );

		session.getTransaction().commit();
		session.close();

		return Arrays.asList(person1, person2);
	}

	private Country getCountByCode( String countryCode ) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Country> cr = cb.createQuery( Country.class );
		Root<Country> root = cr.from( Country.class );
		cr.select( root ).where( cb.equal( root.get( "code" ), countryCode ) );

		Query query = session.createQuery( cr );
		List<Country> results = query.getResultList();

		session.getTransaction().commit();
		session.close();

		return results.isEmpty() ? null : results.get( 0 );
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
