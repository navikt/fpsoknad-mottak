package no.nav.foreldrepenger.selvbetjening.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BrukerInformasjon {
	
	

	private final AktorId aktorId;
	private final Fodselsnummer fnr;

	@JsonCreator
	public BrukerInformasjon(@JsonProperty("aktorId") AktorId aktorId, @JsonProperty("fnr")  Fodselsnummer fnr) {
		this.aktorId = aktorId;
		this.fnr = fnr;
	}	

	public AktorId getAktorId() {
		return aktorId;
	}

	public Fodselsnummer getFnr() {
		return fnr;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [aktorId=" + aktorId + ", fnr=" + fnr + "]";
	}

}
