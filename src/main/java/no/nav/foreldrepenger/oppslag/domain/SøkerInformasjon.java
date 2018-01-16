package no.nav.foreldrepenger.oppslag.domain;

import java.util.List;

public class SøkerInformasjon {

	private final Person person;
	private final List<Income> inntekt;

	public SøkerInformasjon(Person person, List<Income> inntekt) {
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
