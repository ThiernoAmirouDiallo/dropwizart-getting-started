package com.thierno.dropwizard.db.util;

import com.thierno.dropwizard.entity.Message;

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

public class HibernateUtil {

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

				// Apply settings
				registryBuilder.applySettings( settings );

				// Create registry
				registry = registryBuilder.build();

				// Create MetadataSources
				MetadataSources metadataSources = new MetadataSources( registry );
				metadataSources.addAnnotatedClass( Message.class );

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
		schemaExport.createOnly( EnumSet.of( TargetType.SCRIPT ), metadata );
	}
}
