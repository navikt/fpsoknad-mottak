package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Barn {

	private final Fodselsnummer fnr;
	private final Fodselsnummer fnrMor;

	private final LocalDate birthDate;

	public Barn(Fodselsnummer fnrMor, Fodselsnummer fnr, LocalDate birthDate) {
		this.fnr = Objects.requireNonNull(fnr);
		this.birthDate = Objects.requireNonNull(birthDate);
		this.fnrMor = Objects.requireNonNull(fnrMor);
	}

	public Fodselsnummer getFnrMor() {
		return fnrMor;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public Fodselsnummer getFnr() {
		return fnr;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fnr, fnrMor, birthDate);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		Barn that = (Barn) o;
		return Objects.equals(fnr, that.fnr) && Objects.equals(fnrMor, that.fnrMor)
		        && Objects.equals(birthDate, that.birthDate);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [fnr=" + fnr + ", fnrMor=" + fnrMor + ", birthDate=" + birthDate + "]";
	}

}
