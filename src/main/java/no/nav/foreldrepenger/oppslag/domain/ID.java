package no.nav.foreldrepenger.oppslag.domain;

import java.util.Objects;

public class ID {

	private final Pair<AktorId, Fodselsnummer> values;

	public ID(AktorId aktorId, Fodselsnummer fnr) {
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
		return Objects.equals(values, other.values);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [values=" + values + ", aktorId=" + getAktorId() + ", fn)=" + getFnr()
		        + "]";
	}
}
