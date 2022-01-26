package com.thierno.dropwizard.model;

public enum Sexe {

	MALE("M"),
	FEMALE("F");

	String code;

	Sexe( String code ) {
		this.code = code;
	}
}
