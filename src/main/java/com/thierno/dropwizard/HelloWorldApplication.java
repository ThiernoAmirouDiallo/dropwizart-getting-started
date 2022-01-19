package com.thierno.dropwizard;

import com.thierno.dropwizard.api.resources.HelloWorldResource;
import com.thierno.dropwizard.config.HelloWorldConfiguration;
import com.thierno.dropwizard.db.util.Constants;
import com.thierno.dropwizard.db.util.HibernateSessionFactoryUtil;
import com.thierno.dropwizard.domain.entity.Country;
import com.thierno.dropwizard.health.TemplateHealthCheck;
import com.thierno.dropwizard.service.impl.MessageServiceImpl;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
	public void run( HelloWorldConfiguration configuration, Environment environment ) {
		final HelloWorldResource resource = new HelloWorldResource( configuration.getTemplate(), configuration.getDefaultName() );
		final TemplateHealthCheck healthCheck = new TemplateHealthCheck( configuration.getTemplate() );
		environment.jersey().register( resource );
		environment.healthChecks().register( "template", healthCheck );

		// todo change this when adding guice
		MessageServiceImpl.USE_JPA = configuration.getUseJpa();

		// adding countries
		ensureCountriesExist();
	}

	private void ensureCountriesExist() {
		logger.info( "adding non existing countries to db" );
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Constants.COUNTRY_LIST.forEach( country -> {
			Country c = session.find( Country.class, country.getCode() );
			if ( c == null ) {
				session.save( country );
			}
		} );

		session.getTransaction().commit();

		session.close();
		logger.info( "Countries added" );
	}

}
