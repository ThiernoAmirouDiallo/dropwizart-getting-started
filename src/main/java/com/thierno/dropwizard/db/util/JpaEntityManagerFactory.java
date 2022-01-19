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

	public JpaEntityManagerFactory( Class[] entityClasses ) {
		this.entityClasses = entityClasses;
	}

	public EntityManager getEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}

	protected EntityManagerFactory getEntityManagerFactory() {
		PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo( getClass().getSimpleName() );
		Map<String, Object> configuration = new HashMap<>();
		return new EntityManagerFactoryBuilderImpl( new PersistenceUnitInfoDescriptor( persistenceUnitInfo ), configuration ).build();
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
		properties.put( Environment.GENERATE_STATISTICS, "true" );
		properties.put( Environment.DATASOURCE, getPGSimpleDataSource() );

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