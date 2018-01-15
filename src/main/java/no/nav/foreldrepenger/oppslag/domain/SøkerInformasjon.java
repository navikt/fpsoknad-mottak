package no.nav.foreldrepenger.oppslag.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SøkerInformasjon {
	
	private final Person person;
	private final List<Income> inntekt;
	
	@JsonCreator
	public SøkerInformasjon(@JsonProperty("person") Person person, @JsonProperty("inntekt") List<Income> inntekt) {
		this.person = person;
		this.inntekt = inntekt;
	}

	public Person getPerson() {
		return person;
	}

	public List<Income> getInntekt() {
		return inntekt;
	}

}
