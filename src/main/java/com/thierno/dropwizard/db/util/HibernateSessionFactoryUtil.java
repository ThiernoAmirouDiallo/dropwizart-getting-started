package com.thierno.dropwizard.db.util;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

public class HibernateSessionFactoryUtil {

	private static StandardServiceRegistry registry;
	private static SessionFactory sessionFactory;
	private static Metadata metadata;

	public static SessionFactory getSessionFactory() {
		if ( sessionFactory == null ) {
			try {
				// Create registry builder

				StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

				// Hibernate settings equivalent to hibernate.cfg.xml's properties
				Map<String, String> settings = new HashMap<>();
				settings.put( Environment.DRIVER, "org.postgresql.Driver" );
				settings.put( Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect" );
				settings.put( Environment.URL, "jdbc:postgresql://localhost:5432/dropwizard?currentSchema=bookstore" );
				settings.put( Environment.USER, System.getenv( "POSTGRES_USER" ) );
				settings.put( Environment.PASS, System.getenv( "POSTGRES_PASSWORD" ) );
				settings.put( Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread" );
				settings.put( Environment.SHOW_SQL, "true" );
				settings.put( Environment.FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT, "true" );
				settings.put( Environment.GENERATE_STATISTICS, "true" );
				settings.put( Environment.USE_IDENTIFIER_ROLLBACK, "true" ); // if true when em.remove(entity), entity id will reset to null

				//L2 cache
				settings.put( Environment.USE_SECOND_LEVEL_CACHE, "true" );
				settings.put( Environment.JPA_SHARED_CACHE_MODE, "ENABLE_SELECTIVE" ); // enable for certain entities selectively not for all by default
				settings.put( Environment.CACHE_REGION_FACTORY, "org.hibernate.cache.jcache.JCacheRegionFactory" ); // or settings.put( Environment.CACHE_REGION_FACTORY, "jcache" );
				settings.put( "hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider" );
				settings.put( "hibernate.javax.cache.uri", "file:src/main/resources/ehcache.xml" );
				settings.put( Environment.USE_QUERY_CACHE, "true" );

				settings.put( Environment.HBM2DDL_AUTO, "update" );

				// Apply settings
				registryBuilder.applySettings( settings );

				// Create registry
				registry = registryBuilder.build();

				// Create MetadataSources
				MetadataSources metadataSources = new MetadataSources( registry );
				Arrays.asList( Constants.ENTITY_CLASSES ).forEach( metadataSources::addAnnotatedClass );

				// Create Metadata
				metadata = metadataSources.getMetadataBuilder().build();

				// Create SessionFactory
				sessionFactory = metadata.getSessionFactoryBuilder().build();
			} catch ( Exception e ) {
				e.printStackTrace();
				if ( registry != null ) {
					StandardServiceRegistryBuilder.destroy( registry );
				}

				throw e;
			}
		}
		return sessionFactory;
	}

	public static void generateSchemaDDL() {
		getSessionFactory();

		SchemaExport schemaExport = new SchemaExport();
		schemaExport.setFormat( true );
		schemaExport.setOverrideOutputFileContent();
		schemaExport.setOutputFile( "db/generatedSchema.sql" );
		schemaExport.create( EnumSet.of( TargetType.SCRIPT ), metadata );
	}
}
