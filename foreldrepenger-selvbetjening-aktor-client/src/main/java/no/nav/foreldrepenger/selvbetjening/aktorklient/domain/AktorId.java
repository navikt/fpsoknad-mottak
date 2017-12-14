package no.nav.foreldrepenger.selvbetjening.aktorklient.domain;

public class AktorId {

	private final String value;

	public AktorId(String value) {
		this.value = value;
	}

	public String value() {
	   return value;
   }

	@Override
	public String toString() {
		return "AktorId [value=" + value + "]";
	}

}
