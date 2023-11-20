package com.thierno.dropwizard;

import com.sun.net.httpserver.HttpsConfigurator;
import com.thierno.dropwizard.api.resources.HelloWorldResource;
import com.thierno.dropwizard.config.HelloWorldConfiguration;
import com.thierno.dropwizard.db.util.Constants;
import com.thierno.dropwizard.db.util.HibernateSessionFactoryUtil;
import com.thierno.dropwizard.domain.entity.Country;
import com.thierno.dropwizard.health.TemplateHealthCheck;
import com.thierno.dropwizard.service.impl.HibernateServiceImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.exporter.HTTPServer;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

	public static final String PROMETHEUS = "prometheus";
	public static final String KEY_STORE_PATH = "keyStorePath";
	public static final String KEY_STORE_PASSWORD = "keyStorePassword";
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
		logger.info( "exposing prometheus metrics to {}", "https://localhost:8090/metrics" );
		exposePrometheusMetricsWithTLSv1dot2( configuration );
	}

	private void exposePrometheusMetricsWithTLSv1dot2( HelloWorldConfiguration configuration )
			throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {
		Map<String, String> prometheusKeystoreConfig = configuration.getKeyStoreConfig().get( PROMETHEUS );
		String keyStorePath = prometheusKeystoreConfig.get( KEY_STORE_PATH );
		String keyStorePassword = prometheusKeystoreConfig.get( KEY_STORE_PASSWORD );

		SSLContext sslContext = SSLContext.getInstance( "TLSv1.2" );
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
		KeyStore keyStore = KeyStore.getInstance( "PKCS12" );
		keyStore.load( new FileInputStream( keyStorePath ), keyStorePassword.toCharArray() );
		keyManagerFactory.init( keyStore, keyStorePassword.toCharArray() );
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
		trustManagerFactory.init( keyStore );
		sslContext.init( keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom() );
		// for single sided authentication
		//sslContext.init( keyManagerFactory.getKeyManagers(), null, null );
		HttpsConfigurator httpsConfigurator = new HttpsConfigurator( sslContext );

		HTTPServer server = new HTTPServer.Builder() //
				.withHttpsConfigurator( httpsConfigurator ) //
				.withPort( 8090 ) //
				.build();
	}

	private void ensureCountriesExist() {
		logger.info( "adding non existing countries to db" );
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Constants.COUNTRY_LIST.forEach( country -> {
			List<Country> countries = session.createQuery( "select c from Country c where c.code = :country_code" ).setParameter( "country_code", country.getCode() ).getResultList();

			if ( countries.isEmpty() ) {
				session.save( country );
			}
		} );

		session.getTransaction().commit();

		session.close();
		logger.info( "Countries added" );
	}

}
