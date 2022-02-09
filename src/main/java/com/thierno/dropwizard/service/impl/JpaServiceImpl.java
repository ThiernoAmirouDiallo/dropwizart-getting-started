package com.thierno.dropwizard.service.impl;

import com.thierno.dropwizard.db.util.HibernateEntityManagerFactoryUtil;
import com.thierno.dropwizard.domain.entity.Child;
import com.thierno.dropwizard.domain.entity.CompositeName;
import com.thierno.dropwizard.domain.entity.Guide;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.domain.entity.Parent;
import com.thierno.dropwizard.domain.entity.Person;
import com.thierno.dropwizard.domain.entity.Student;
import com.thierno.dropwizard.domain.entity.inhetancemapping.Animal;
import com.thierno.dropwizard.domain.entity.inhetancemapping.Cat;
import com.thierno.dropwizard.domain.entity.inhetancemapping.Dog;
import com.thierno.dropwizard.service.JpaService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaServiceImpl implements JpaService {

	Logger logger = LoggerFactory.getLogger( JpaServiceImpl.class );

	public static boolean USE_JPA = false;

	@Override
	public List<Student> testJpa() {
		return jpaBatchAndFetchSizeTest();
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

	private List<Parent> jpaCriteriaApiSelectEntity() {
		new HibernateServiceImpl().manyToOneAndEmbededIdTest();

		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();
		entityManager.getTransaction().begin();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Parent> parentCriteriaQuery = criteriaBuilder.createQuery( Parent.class );
		Root<Parent> parentRoot = parentCriteriaQuery.from( Parent.class );
		//Path<Parent> name = parentRoot.get( "name" );
		parentCriteriaQuery.select( parentRoot );

		TypedQuery<Parent> query = entityManager.createQuery( parentCriteriaQuery );

		List<Parent> parents = query.getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		return parents;
	}

	private List<String> jpaCriteriaApiTestSelectOneField() {
		new HibernateServiceImpl().manyToOneAndEmbededIdTest();

		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();
		entityManager.getTransaction().begin();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<String> parentCriteriaQuery = criteriaBuilder.createQuery( String.class );
		Root<Parent> parentRoot = parentCriteriaQuery.from( Parent.class );
		Path<String> firstName = parentRoot.get( "id" ).get( "firstName" );
		parentCriteriaQuery.select( firstName );

		TypedQuery<String> query = entityManager.createQuery( parentCriteriaQuery );

		List<String> names = query.getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		return names;
	}

	private List<Parent> jpaCriteriaApiFilteringAndJoiningResult() {
		new HibernateServiceImpl().manyToOneAndEmbededIdTest();

		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();
		entityManager.getTransaction().begin();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Parent> parentCriteriaQuery = criteriaBuilder.createQuery( Parent.class ) //
				//.distinct( true ) // remove duplicate in the join of parents that have multiple children
				;

		Root<Parent> parentRoot = parentCriteriaQuery.from( Parent.class );
		//Join<Parent, Child> parentChildJoin = parentRoot.join( "children" ); // parentRoot.join( "child", JoinType.LEFT)
		Fetch<Parent, Child> parentChildFetch = parentRoot.fetch( "children" ); // to fetch eagerly or parentRoot.fetch( "child", JoinType.LEFT)

		//parentCriteriaQuery.select( criteriaBuilder.count( parentRoot ) );
		//parentCriteriaQuery.select( criteriaBuilder.max(  parentRoot.get( "id" ).get("firstName") ) );
		parentCriteriaQuery.select( parentRoot );

		Path<String> firstName = parentRoot.get( "id" ).get( "firstName" );
		Path<String> lastName = parentRoot.get( "id" ).get( "lastName" );
		//parentCriteriaQuery.where( criteriaBuilder.equal( firstName, "Thierno" ) );
		parentCriteriaQuery.where( //
				criteriaBuilder.and( //
						criteriaBuilder.like( firstName, "Thier%" ), //
						criteriaBuilder.equal( lastName, criteriaBuilder.parameter( String.class, "lastName" ) ) ) );

		TypedQuery<Parent> query = entityManager.createQuery( parentCriteriaQuery ) //
				.setParameter( "lastName", "Diallo" );

		List<Parent> parents = query.getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		return parents;
	}

	private List<Student> jpaBatchAndFetchSizeTest() {
		//adding some guides and students
		ensureStudentAndGuidesExist( 4 );

		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();
		entityManager.getTransaction().begin();

		//List<Student> students = entityManager.createQuery( "select s FROM Student s", Student.class ).getResultList();
		List<Student> students = entityManager.createQuery( "select student FROM Student student left join fetch student.guide guide", Student.class ).getResultList();
		//List<Guide> guides = entityManager.createQuery( "select guide FROM Guide guide join fetch guide.students", Guide.class ).getResultList();

		for ( Student student : students ) {
			logger.info( "Student: {}, Guide: {} {}", student.getName(), student.getGuide().getName().getFirstName(), student.getGuide().getName().getLastName() );
		}

		entityManager.getTransaction().commit();
		entityManager.close();

		return students;
	}

	void ensureStudentAndGuidesExist( int n ) {
		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();
		entityManager.getTransaction().begin();
		entityManager.createQuery( "delete from Student s" ).executeUpdate();
		entityManager.createQuery( "delete from Guide g" ).executeUpdate();

		for ( int i = 0; i < n; i++ ) {
			Guide guide = Guide.builder().name( //
					CompositeName.builder()//
							.firstName( "Thierno" ) //
							.lastName( String.format( "Diallo_%s", i + 1 ) ) //
							.build() //
			).build();

			Student student = Student.builder() //
					.name( String.format( "Child_%s", i + 1 ) ) //
					.guide( guide ) //
					.build();

			entityManager.persist( student );
		}

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private List<Animal> jpaInheritenceMappingTest() {
		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();
		entityManager.getTransaction().begin();

		Animal dog = Dog.builder() //
				.name( "Dogy" ).build();

		Animal cat = Cat.builder() //
				.name( "Caty" ).build();

		entityManager.persist( dog );
		entityManager.persist( cat );

		List<Animal> animals = entityManager.createQuery( "select animal FROM Animal animal", Animal.class ).getResultList();

		Dog fetchedDog = entityManager.createQuery( "select dog FROM Dog dog", Dog.class ) //
				.setFirstResult( 0 ) //
				.setMaxResults( 1 ) //
				.getSingleResult();

		Cat FetchedCat = entityManager.createQuery( "select cat FROM Cat cat", Cat.class ) //
				.setFirstResult( 0 ) //
				.setMaxResults( 1 ) //
				.getSingleResult();

		entityManager.getTransaction().commit();
		entityManager.close();

		return animals;
	}

	private List<String[]> jpaCriteriaApiTestSelectMultipleFields() {
		new HibernateServiceImpl().manyToOneAndEmbededIdTest();

		EntityManager entityManager = HibernateEntityManagerFactoryUtil.getJpaEntityManager();
		entityManager.getTransaction().begin();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Object[]> parentCriteriaQuery = criteriaBuilder.createQuery( Object[].class );
		Root<Parent> parentRoot = parentCriteriaQuery.from( Parent.class );
		Path<String> firstName = parentRoot.get( "id" ).get( "firstName" );
		Path<String> lastName = parentRoot.get( "id" ).get( "lastName" );

		//parentCriteriaQuery.select( parentRoot.get( "id" ) ); // would return List<ParentCompositeId>

		//parentCriteriaQuery.select( criteriaBuilder.array( firstName, lastName ) );
		parentCriteriaQuery.multiselect( firstName, lastName );

		TypedQuery<Object[]> query = entityManager.createQuery( parentCriteriaQuery );

		List<Object[]> names = query.getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		return names.stream() //
				.map( objects -> Arrays.copyOf( objects, objects.length, String[].class ) ) //
				.collect( Collectors.toList() );
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

		Parent parent2 = (Parent) entityManager.createNativeQuery( "select * from Parent as parent where parent.lastName = ?1", Parent.class ) //
				.setParameter( 1, "Diallo" ) //
				.getSingleResult();

		Parent parent3 = (Parent) entityManager.createNamedQuery( "findParentByLastName",
						Parent.class ) // named queries defined by Constants.NAMED_QUERIES_MAP and in the Parent class level annotation @NamedQueries
				.setParameter( "lastName", "Diallo" ) //
				.getSingleResult();

		Long peopleCount = (Long) entityManager.createQuery( "select count(person) from Person person" ).getSingleResult();

		List<Child> studentList = entityManager.createQuery( "select child from Child child join child.parent parent" ).getResultList();
		List<Child> studentList2 = entityManager.createQuery( "select child from Child child left join child.parent parent", Child.class ).getResultList();
		List<Child> studentList3 = entityManager.createQuery( "select child from Child child join fetch child.parent parent", Child.class ).getResultList();
		List<Child> studentList4 = entityManager.createQuery( "select child from Child child left join fetch child.parent parent", Child.class ).getResultList();

		//entityManager.setFlushMode( FlushModeType.AUTO ); // AUTO (commit befor issuing queries to database)|COMMIT

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
