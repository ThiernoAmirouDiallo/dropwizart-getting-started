package com.thierno.dropwizard.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.db.util.HibernateSessionFactoryUtil;
import com.thierno.dropwizard.domain.entity.Guide;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.model.Saying;
import com.thierno.dropwizard.service.HibernateService;
import com.thierno.dropwizard.service.JpaService;
import com.thierno.dropwizard.service.impl.HibernateServiceImpl;
import com.thierno.dropwizard.service.impl.JpaServiceImpl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

	private final String template;
	private final String defaultName;
	private final AtomicLong counter;

	// todo inject this in the future
	HibernateService hibernateService = new HibernateServiceImpl();
	JpaService jpaService = new JpaServiceImpl();

	public HelloWorldResource( String template, String defaultName ) {
		this.template = template;
		this.defaultName = defaultName;
		this.counter = new AtomicLong();
	}

	@GET()
	@Timed
	public Saying sayHello( @QueryParam("name") Optional<String> name ) {
		final String value = String.format( template, name.orElse( defaultName ) );
		hibernateService.saveMessage( value );
		return new Saying( counter.incrementAndGet(), value );
	}

	@GET()
	@Timed
	@Path("genarateSchemaDdl")
	public Saying genarateSchemaDdl() {
		HibernateSessionFactoryUtil.generateSchemaDDL();
		return new Saying( counter.incrementAndGet(), "Ok" );
	}

	@GET()
	@Timed
	@Path("testHibernate")
	public Message testHibernate() throws JsonProcessingException {
		return hibernateService.testHibernate();
	}

	@GET()
	@Timed
	@Path("testJpa")
	public Guide testJpa() throws JsonProcessingException {
		return jpaService.testJpa();
	}
}

