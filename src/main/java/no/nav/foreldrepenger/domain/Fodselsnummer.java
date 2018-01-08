package no.nav.foreldrepenger.domain;

import java.util.Objects;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Fodselsnummer {

	@Size(min = 11, max = 11)
	private final String fnr;

	@JsonCreator
	public Fodselsnummer(String fnr) {
		this.fnr = fnr;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(fnr);
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
		Fodselsnummer other = (Fodselsnummer) obj;
		if (fnr == null) {
			if (other.fnr != null) {
				return false;
			}
		} else if (!fnr.equals(other.fnr)) {
			return false;
		}
		return true;
	}

	@JsonValue
	public String getFnr() {
		return fnr;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [fnr=" + fnr + "]";
	}

}
