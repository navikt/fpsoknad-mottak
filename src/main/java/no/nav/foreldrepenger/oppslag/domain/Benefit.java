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

public class Benefit {

	private String type;
	private String status;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate from;
	private Optional<LocalDate> to;

	public Benefit(String type, String status, LocalDate from) {
		this(type, status, from, Optional.empty());
	}

	@JsonCreator
	public Benefit(@JsonProperty("type") String type, @JsonProperty("status") String status,
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
	public String toString() {
		return "Ytelse{" + "type='" + type + '\'' + ", status='" + status + '\'' + ", from=" + from + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		Benefit benefit = (Benefit) o;
		return Objects.equals(type, benefit.type) && Objects.equals(status, benefit.status)
		        && Objects.equals(from, benefit.from) && Objects.equals(to, benefit.to);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, status, from, to);
	}
}
