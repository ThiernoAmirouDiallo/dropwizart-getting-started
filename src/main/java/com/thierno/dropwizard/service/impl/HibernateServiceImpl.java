package com.thierno.dropwizard.service.impl;

import com.thierno.dropwizard.db.util.HibernateEntityManagerFactoryUtil;
import com.thierno.dropwizard.db.util.HibernateSessionFactoryUtil;
import com.thierno.dropwizard.domain.entity.Child;
import com.thierno.dropwizard.domain.entity.CompositeName;
import com.thierno.dropwizard.domain.entity.Country;
import com.thierno.dropwizard.domain.entity.Department;
import com.thierno.dropwizard.domain.entity.Employee;
import com.thierno.dropwizard.domain.entity.EmployeeCompositeId;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.domain.entity.Movie;
import com.thierno.dropwizard.domain.entity.Parent;
import com.thierno.dropwizard.domain.entity.Passport;
import com.thierno.dropwizard.domain.entity.Person;
import com.thierno.dropwizard.domain.entity.Student;
import com.thierno.dropwizard.model.Sexe;
import com.thierno.dropwizard.service.HibernateService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateServiceImpl implements HibernateService {

	Logger logger = LoggerFactory.getLogger( HibernateServiceImpl.class );
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

	private Message l2Caching() {
		long messageId = saveMessageSession( String.format( "message id %s", UUID.randomUUID() ) );

		Session session1 = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session1.beginTransaction();

		Message message1 = session1.find( Message.class, messageId );

		session1.getTransaction().commit();
		session1.close();

		Session session2 = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session2.beginTransaction();

		Message message2 = session2.find( Message.class, messageId );

		session2.getTransaction().commit();
		session2.close();

		return message1;
	}

	private List<Student> batchFetchingWithScrolableResultTest() {
		//adding some guides and students
		new JpaServiceImpl().ensureStudentAndGuidesExist( 4 );

		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Student> studentCriteriaQuery = criteriaBuilder.createQuery( Student.class );
		Root<Student> studentRoot = studentCriteriaQuery.from( Student.class );
		studentRoot.fetch( "guide" );
		studentCriteriaQuery.select( studentRoot );

		TypedQuery<Student> query = session.createQuery( studentCriteriaQuery );
		org.hibernate.query.Query<Student> hibernateQuery = query.unwrap( org.hibernate.query.Query.class );
		hibernateQuery.setFetchSize( 5 );

		ScrollableResults results = hibernateQuery.scroll( ScrollMode.FORWARD_ONLY );

		List<Student> studentList = new ArrayList<>();

		while ( results.next() ) {
			Student currentStudent = (Student) results.get( 0 );

			logger.info( "Current Student: {}", currentStudent );
			studentList.add( currentStudent );
		}

		session.getTransaction().commit();
		session.close();

		return studentList;
	}

	List<Child> manyToOneAndEmbededIdTest() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Parent parent = Parent.builder().id( //
				CompositeName.builder()//
						.firstName( "Thierno" ) //
						.lastName( String.format( "Diallo_%s", UUID.randomUUID() ) ) //
						.build() //
		).build();

		Child child = Child.builder() //
				.name( "Child" ) //
				.parent( parent ) //
				.build();

		session.persist( child );

		session.getTransaction().commit();
		session.close();

		return Arrays.asList( child );
	}

	private List<Employee> manyToOneAndEmbededIdWithMapsIdTest() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Department department = Department.builder().name( "IT" ) //
				.build();

		Employee employee = Employee.builder() //
				.id( EmployeeCompositeId.builder().employeeName( "thiernoamiroudiallo" ).build() ) //
				.name( "Thierno Amirou Diallo" )
				.department( department ) //
				.build();

		session.persist( employee );

		session.getTransaction().commit();
		session.close();

		return Arrays.asList( employee );
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

	List<Person> manyToManyTest() {
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
		person1.getNicknames2().add( "Server Engineer" );
		person1.getNicknamesMap().put( "Server Engineer", "Backend engineer" );
		session.persist( person1 );

		Person person2 = Person.builder().firstName( "Amirou" ) //
				.lastName( "Diallo" ) //
				.sexe( Sexe.FEMALE )
				.country( getCountByCode( "GN" ) ) //
				.passport( Passport.builder().expirationDate( LocalDate.now().plusYears( 5 ).minusDays( 1 ) ).build() ) //
				.build();
		person2.getPassport().setPerson( person2 );
		person2.getMovies().add( movie1 );
		person2.getNicknames().add( "SWE" );
		person2.getNicknames().add( "Programmer" );
		person2.getNicknames2().add( "SWE" );
		person2.getNicknames2().add( "Programmer" );
		person2.getNicknamesMap().put( "SWE", "Software engineer" );
		person2.getNicknamesMap().put( "Programmer", "Computer programmer" );
		session.persist( person2 );

		session.getTransaction().commit();
		session.close();

		Session session2 = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session2.beginTransaction();
		Person retriedPerson1 = session2.find( Person.class, person2.getId() );
		session2.getTransaction().commit();
		session2.close();

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

	long saveMessageSession( String messageValue ) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Message message = new Message();
		message.setMessage( messageValue );
		session.save( message );
		session.getTransaction().commit();
		session.close();
		logger.info( "Using Hibernate Session - logging using logback" );

		return message.getId();
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
