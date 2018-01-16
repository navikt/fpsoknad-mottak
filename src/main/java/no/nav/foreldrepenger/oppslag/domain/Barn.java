package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Barn {

	private final Fodselsnummer fnr;
	private final Fodselsnummer fnrMor;

	private final LocalDate birthDate;

	public Barn(Fodselsnummer fnrMor, Fodselsnummer fnr, LocalDate birthDate) {
		this.fnr = fnr;
		this.birthDate = birthDate;
		this.fnrMor = fnrMor;
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
	public String toString() {
		return getClass().getSimpleName() + " [fnr=" + fnr + ", fnrMor=" + fnrMor + ", birthDate=" + birthDate + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(fnr, fnrMor, birthDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Barn other = (Barn) obj;
		if (birthDate == null) {
			if (other.birthDate != null) {
				return false;
			}
		} else if (!birthDate.equals(other.birthDate)) {
			return false;
		}
		if (fnr == null) {
			if (other.fnr != null) {
				return false;
			}
		} else if (!fnr.equals(other.fnr)) {
			return false;
		}
		if (fnrMor == null) {
			if (other.fnrMor != null) {
				return false;
			}
		} else if (!fnrMor.equals(other.fnrMor)) {
			return false;
		}
		return true;
	}

}
