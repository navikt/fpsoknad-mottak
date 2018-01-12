package no.nav.foreldrepenger.oppslag.domain;

public enum Kjonn {
	M("M"), K("M");

	private final String verdi;

	Kjonn(String verdi) {
		this.verdi = verdi;
	}

	public String getVerdi() {
		return verdi;
	}
}
