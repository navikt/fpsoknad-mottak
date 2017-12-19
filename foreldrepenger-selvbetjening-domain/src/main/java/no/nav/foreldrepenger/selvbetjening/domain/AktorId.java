package no.nav.foreldrepenger.selvbetjening.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public class AktorId {

	private final String value;

	@JsonCreator
	public AktorId(String value) {
		this.value = value;
	}

	public String getValue() {
	   return value;
   }

	@Override
	public String toString() {
		return "AktorId [value=" + value + "]";
	}

}
