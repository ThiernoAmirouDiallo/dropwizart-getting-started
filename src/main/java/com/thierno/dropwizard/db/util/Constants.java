package com.thierno.dropwizard.db.util;

import com.thierno.dropwizard.domain.entity.Country;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.domain.entity.Passport;
import com.thierno.dropwizard.domain.entity.Person;

import java.util.ArrayList;
import java.util.List;

public class Constants {

	public static final List<Country> COUNTRY_LIST;
	public static final Class[] ENTITY_CLASSES = new Class[] { //
			Message.class, //
			Person.class, //
			Country.class, //
			Passport.class //
	};

	static {
		COUNTRY_LIST = new ArrayList<>();
		COUNTRY_LIST.add( Country.builder().code( "GN" ).name( "Guinea" ).build() );
		COUNTRY_LIST.add( Country.builder().code( "CA" ).name( "Canada" ).build() );
		COUNTRY_LIST.add( Country.builder().code( "FR" ).name( "France" ).build() );
		COUNTRY_LIST.add( Country.builder().code( "US" ).name( "United States" ).build() );
	}
}
