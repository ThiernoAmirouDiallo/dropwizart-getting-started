package com.thierno.dropwizard.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.thierno.dropwizard.db.util.HibernateSessionFactoryUtil;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.domain.entity.Person;
import com.thierno.dropwizard.model.Saying;
import com.thierno.dropwizard.service.HibernateService;
import com.thierno.dropwizard.service.JpaService;
import com.thierno.dropwizard.service.impl.HibernateServiceImpl;
import com.thierno.dropwizard.service.impl.JpaServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

	Logger logger = LoggerFactory.getLogger( HelloWorldResource.class );

	private final String template;
	private final String defaultName;
	private final AtomicLong counter;

	//COUNTER
	static final Counter requests = Counter.build() //
			.name( "java_app_requests_total" ) //
			.labelNames( "endpoint" ) //
			.help( "Total requests." ) //
			.register();

	//GAUGES
	static final Gauge inprogressRequests = Gauge.build() //
			.name( "inprogress_requests" ) //
			.labelNames( "endpoint" ) //
			.help( "Inprogress requests." ) //
			.register();
	static final Gauge inprogressRequestsTimer = Gauge.build() //
			.name( "inprogress_requests_timer" ) //
			.labelNames( "endpoint" ) //
			.help( "Inprogress requests timer." ) //
			.register();

	//SUMMARY
	static final Summary requestLatencySummary = Summary.build() //
			.name( "requests_latency_seconds_summary" ) //
			.labelNames( "endpoint" ) //
			.help( "request latency in seconds" ) //
			.quantile( 0.5, 0.01 )    // 0.5 quantile (median) with 0.01 allowed error
			.quantile( 0.95, 0.005 )  // 0.95 quantile with 0.005 allowed error
			.register();

	// HISTOGRAM
	static final Histogram requestLatencyHistogram = Histogram.build() //
			.name( "requests_latency_seconds_histogram" ) //
			.labelNames( "endpoint", "jobName" ) //
			//.buckets() // to set our own wanted buckets
			.help( "Request latency in seconds." ) //
			.register();

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
	public List<Person> testHibernate() throws JsonProcessingException {
		return hibernateService.testHibernate();
	}

	@GET()
	@Timed
	@Path("testJpa")
	public Message testJpa() throws JsonProcessingException {
		return jpaService.testJpa();
	}

	@GET()
	@Path("testPrometheusCounter")
	public Message testPrometheusCounter() throws JsonProcessingException {
		requests.labels( "testPrometheus" ).inc();
		return jpaService.testJpa();
	}

	@GET()
	@Path("testPrometheusGauge")
	public Message testPrometheusGauge() throws JsonProcessingException {
		inprogressRequests.labels( "testPrometheus" ).inc();
		inprogressRequestsTimer.labels( "testPrometheus" );
		Gauge.Timer timer = inprogressRequestsTimer.labels( "testPrometheus" ).startTimer();

		// artrary processing
		arbitraryProcessing();

		timer.setDuration();
		inprogressRequests.labels( "testPrometheus" ).dec();
		return jpaService.testJpa();
	}

	@GET()
	@Path("testPrometheusSummary")
	public Message testPrometheusSummary() throws JsonProcessingException {
		Summary.Timer requestTimer = requestLatencySummary.labels( "testPrometheus" ).startTimer();

		try {
			// artrary processing
			arbitraryProcessing();

			return jpaService.testJpa();
		} finally {
			requestTimer.observeDuration();
		}
	}

	@GET()
	@Path("testPrometheusHistogram")
	public Message testPrometheusHistogram( @QueryParam("jobName") String jobName ) throws JsonProcessingException {
		Histogram.Timer requestTimer = requestLatencyHistogram.labels( "testPrometheus", jobName ).startTimer();

		try {
			// artrary processing
			arbitraryProcessing();

			return jpaService.testJpa();
		} finally {
			requestTimer.observeDuration();
		}
	}

	private void arbitraryProcessing() {
		try {
			// to sleep between 0 - 5 seconds
			long waitingMillis = RandomUtils.nextLong() % 5_000;
			logger.info( "Waiting for {} millis", waitingMillis );
			Thread.sleep( waitingMillis );
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
	}
}

