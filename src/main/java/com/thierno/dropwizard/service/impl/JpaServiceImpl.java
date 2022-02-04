package com.thierno.dropwizard.service.impl;

import com.thierno.dropwizard.db.util.HibernateEntityManagerFactoryUtil;
import com.thierno.dropwizard.domain.entity.Child;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.domain.entity.Parent;
import com.thierno.dropwizard.domain.entity.Person;
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
	public List<Parent> testJpa() {
		return jpaCriteriaApiFilteringAndJoiningResult();
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
