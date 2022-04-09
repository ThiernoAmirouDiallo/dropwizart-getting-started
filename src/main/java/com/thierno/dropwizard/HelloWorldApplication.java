package com.thierno.dropwizard;

import com.thierno.dropwizard.api.resources.HelloWorldResource;
import com.thierno.dropwizard.config.HelloWorldConfiguration;
import com.thierno.dropwizard.db.util.Constants;
import com.thierno.dropwizard.db.util.HibernateSessionFactoryUtil;
import com.thierno.dropwizard.domain.entity.Country;
import com.thierno.dropwizard.health.TemplateHealthCheck;
import com.thierno.dropwizard.service.impl.HibernateServiceImpl;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.exporter.HTTPServer;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

	Logger logger = LoggerFactory.getLogger( HelloWorldApplication.class );

	public static void main( String[] args ) throws Exception {
		new HelloWorldApplication().run( args );
	}

	@Override
	public String getName() {
		return "hello-world";
	}

	@Override
	public void initialize( Bootstrap<HelloWorldConfiguration> bootstrap ) {
		// nothing to do yet
	}

	@Override
	public void run( HelloWorldConfiguration configuration, Environment environment ) throws Exception {
		final HelloWorldResource resource = new HelloWorldResource( configuration.getTemplate(), configuration.getDefaultName() );
		final TemplateHealthCheck healthCheck = new TemplateHealthCheck( configuration.getTemplate() );
		environment.jersey().register( resource );
		environment.healthChecks().register( "template", healthCheck );

		// todo change this when adding guice
		HibernateServiceImpl.USE_JPA = configuration.getUseJpa();

		//dump schema ddl
		HibernateSessionFactoryUtil.generateSchemaDDL();

		// adding countries
		ensureCountriesExist();

		// exposing prometheus metrics
		logger.info( "exposing prometheus metrics to {}", "http://localhost:8090/metrics" );
		HTTPServer server = new HTTPServer.Builder().withPort( 8090 ).build();
	}

	private void ensureCountriesExist() {
		logger.info( "adding non existing countries to db" );
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Constants.COUNTRY_LIST.forEach( country -> {
			List<Country> countries = session.createQuery("select c from Country c where c.code = :country_code")
					.setParameter( "country_code", country.getCode())
					.getResultList();

			if ( countries.isEmpty() ) {
				session.save( country );
			}
		} );

		session.getTransaction().commit();

		session.close();
		logger.info( "Countries added" );
	}

}
