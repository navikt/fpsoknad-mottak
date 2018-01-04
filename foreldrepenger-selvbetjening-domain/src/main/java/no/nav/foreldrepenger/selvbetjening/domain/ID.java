package no.nav.foreldrepenger.selvbetjening.domain;

import java.util.Objects;

import javax.validation.Valid;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ID {

	private final Pair<AktorId, Fodselsnummer> values;

	@JsonCreator
	public ID(@JsonProperty("aktorId") AktorId aktorId, @JsonProperty("fnr") Fodselsnummer fnr) {
		values = Pair.of(aktorId, fnr);
	}

	public AktorId getAktorId() {
		return values.getFirst();
	}

	public Fodselsnummer getFnr() {
		return values.getSecond();
	}


	@Override
	public int hashCode() {
		return Objects.hash(values);
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
		ID other = (ID) obj;
		if (values == null) {
			if (other.values != null) {
				return false;
			}
		} else if (!values.equals(other.values)) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "ID [values=" + values + ", aktorId=" + getAktorId() + ", fn)=" + getFnr() + "]";
	}	
}
