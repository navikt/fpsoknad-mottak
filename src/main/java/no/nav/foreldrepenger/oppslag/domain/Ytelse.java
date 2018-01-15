package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

public class Ytelse {

	private String type;
	private String status;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate from;
	private Optional<LocalDate> to;
	private String origin;

	public Ytelse(String type, String status, LocalDate from, String origin) {
		this(origin, origin, from, null, origin);
	}

	@JsonCreator
	public Ytelse(@JsonProperty("type") String type, @JsonProperty("status") String status,
	        @JsonProperty("from") LocalDate from, @JsonProperty("to") Optional<LocalDate> to,
	        @JsonProperty("origin") String origin) {
		this.type = type;
		this.status = status;
		this.from = from;
		this.to = to;
		this.origin = origin;
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

	public String getOrigin() {
		return origin;
	}

	@Override
	public String toString() {
		return "Ytelse{" + "type='" + type + '\'' + ", status='" + status + '\'' + ", from=" + from + ", to=" + to
		        + ", origin='" + origin + '\'' + '}';
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
		        && Objects.equals(from, ytelse.from) && Objects.equals(to, ytelse.to)
		        && Objects.equals(origin, ytelse.origin);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, status, from, to, origin);
	}
}
