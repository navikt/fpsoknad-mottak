package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ytelse {

	private final String type;
	private final String status;
	private final LocalDate from;
	private final Optional<LocalDate> to;

	public Ytelse(String type, String status, LocalDate from) {
		this(type, status, from, Optional.empty());
	}

	@JsonCreator
	public Ytelse(@JsonProperty("type") String type, @JsonProperty("status") String status,
	        @JsonProperty("from") LocalDate from, @JsonProperty("to") Optional<LocalDate> to) {
		this.type = type;
		this.status = status;
		this.from = from;
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public String getStatus() {
		return status;
	}

	public LocalDate getFrom() {
		return from;
	}

	public Optional<LocalDate> getTo() {
		return to;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		Ytelse ytelse = (Ytelse) o;
		return Objects.equals(type, ytelse.type) && Objects.equals(status, ytelse.status)
		        && Objects.equals(from, ytelse.from) && Objects.equals(to, ytelse.to);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, status, from, to);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [type=" + type + ", status=" + status + ", from=" + from + ", to=" + to
		        + "]";
	}
}
