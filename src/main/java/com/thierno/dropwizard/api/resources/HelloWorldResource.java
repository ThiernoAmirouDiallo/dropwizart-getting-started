package com.thierno.dropwizard.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.db.util.HibernateSessionFactoryUtil;
import com.thierno.dropwizard.domain.entity.Child;
import com.thierno.dropwizard.model.Saying;
import com.thierno.dropwizard.service.MessageService;
import com.thierno.dropwizard.service.impl.MessageServiceImpl;

import java.util.List;
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
	MessageService messageService = new MessageServiceImpl();

	public HelloWorldResource( String template, String defaultName ) {
		this.template = template;
		this.defaultName = defaultName;
		this.counter = new AtomicLong();
	}

	@GET()
	@Timed
	public Saying sayHello( @QueryParam("name") Optional<String> name ) {
		final String value = String.format( template, name.orElse( defaultName ) );
		messageService.saveMessage( value );
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
	public List<Child> testHibernate() throws JsonProcessingException {
		return messageService.testHibernate();
	}
}

