package com.thierno.dropwizard.db.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.hibernate.cfg.Environment;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.postgresql.ds.PGSimpleDataSource;

import lombok.Data;

@Data
public class JpaEntityManagerFactory {

	private String DB_URL = "jdbc:postgresql://localhost:5432/dropwizard?currentSchema=bookstore";
	private String DB_USER_NAME = System.getenv( "POSTGRES_USER" );
	private String DB_PASSWORD = System.getenv( "POSTGRES_PASSWORD" );
	private Class[] entityClasses;
	private Map<String, String> namedQueriesMap;

	public JpaEntityManagerFactory( Class[] entityClasses, Map<String, String> namedQueriesMap ) {
		this.entityClasses = entityClasses;
		this.namedQueriesMap = namedQueriesMap;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo( getClass().getSimpleName() );
		Map<String, Object> configuration = new HashMap<>();
		EntityManagerFactory entityManagerFactory = new EntityManagerFactoryBuilderImpl( new PersistenceUnitInfoDescriptor( persistenceUnitInfo ), configuration ).build();

		registerNamedQueries( entityManagerFactory );

		return entityManagerFactory;
	}

	private void registerNamedQueries( EntityManagerFactory entityManagerFactory ) {
		//registring named Queries
		EntityManager em = entityManagerFactory.createEntityManager();
		namedQueriesMap.forEach( ( name, query ) -> entityManagerFactory.addNamedQuery( name, em.createQuery( query ) ) );
		em.close();
	}

	protected HibernatePersistenceUnitInfo getPersistenceUnitInfo( String name ) {
		return new HibernatePersistenceUnitInfo( name, getEntityClassNames(), getProperties() );
	}

	protected List<String> getEntityClassNames() {
		return Arrays.asList( getEntities() ).stream().map( Class::getName ).collect( Collectors.toList() );
	}

	protected Properties getProperties() {
		Properties properties = new Properties();
		properties.put( Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect" );
		properties.put( Environment.USE_NEW_ID_GENERATOR_MAPPINGS, false );
		properties.put( Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread" );
		properties.put( Environment.SHOW_SQL, "true" );
		properties.put( Environment.FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT, "true" );
		properties.put( Environment.GENERATE_STATISTICS, "false" );
		properties.put( Environment.DATASOURCE, getPGSimpleDataSource() );

		//L2 cache
		properties.put( Environment.USE_SECOND_LEVEL_CACHE, "true" );
		properties.put( Environment.JPA_SHARED_CACHE_MODE, "ENABLE_SELECTIVE" ); // enable for certain entities selectively not for all by default
		properties.put( Environment.CACHE_REGION_FACTORY, "org.hibernate.cache.jcache.JCacheRegionFactory" ); // or properties.put( Environment.CACHE_REGION_FACTORY, "jcache" );
		properties.put( "hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider" );
		properties.put( "hibernate.javax.cache.missing_cache_strategy", "create" );
		properties.put( "hibernate.javax.cache.uri", "file:src/main/resources/ehcache.xml" );

		properties.put( Environment.HBM2DDL_AUTO, "update" );

		return properties;
	}

	protected Class[] getEntities() {
		return entityClasses;
	}

	protected DataSource getPGSimpleDataSource() {
		PGSimpleDataSource mysqlDataSource = new PGSimpleDataSource();
		mysqlDataSource.setURL( DB_URL );
		mysqlDataSource.setUser( DB_USER_NAME );
		mysqlDataSource.setPassword( DB_PASSWORD );
		return mysqlDataSource;
	}
}