package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonPropertyOrder({ "id" })
public class Person {

	//@JsonUnwrapped
	private final ID id;

	private final Kjonn kjonn;
	private final LocalDate fodselsdato;

	private final Adresse adresse;
	//@JsonUnwrapped
	private final Navn navn;
	private final List<Barn> barn;

	@JsonCreator
	public Person(@JsonProperty("ids") ID id, @JsonProperty("kjonn") Kjonn kjonn, @JsonProperty("navn") Navn navn,
	        @JsonProperty("adresse") Adresse adresse, @JsonProperty("fodselsdato") LocalDate fodselsdato,
	        @JsonProperty("barn") List<Barn> barn) {
		this.id = id;
		this.kjonn = kjonn;
		this.adresse = adresse;
		this.navn = navn;
		this.fodselsdato = fodselsdato;
		this.barn = barn;
	}

	public Kjonn getKjonn() {
		return kjonn;
	}
	
	public Person(ID id, Kjonn kjonn, Navn name, LocalDate fodselsdato, Adresse adresse) {
		this(id, kjonn, name, adresse, fodselsdato, Collections.emptyList());
	}

	public ID getId() {
		return id;
	}

	public LocalDate getFodselsdato() {
		return fodselsdato;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, kjonn, adresse, navn, fodselsdato, barn);
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
		Person other = (Person) obj;
		if (adresse == null) {
			if (other.adresse != null) {
				return false;
			}
		} else if (!adresse.equals(other.adresse)) {
			return false;
		}
		if (barn == null) {
			if (other.barn != null) {
				return false;
			}
		} else if (!barn.equals(other.barn)) {
			return false;
		}
		if (fodselsdato == null) {
			if (other.fodselsdato != null) {
				return false;
			}
		} else if (!fodselsdato.equals(other.fodselsdato)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}

		if (!kjonn.equals(other.kjonn)) {
			return false;
		}
		if (navn == null) {
			if (other.navn != null) {
				return false;
			}
		} else if (!navn.equals(other.navn)) {
			return false;
		}
		return true;
	}

	public List<Barn> getBarn() {
		return barn;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public Navn getNavn() {
		return navn;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", fodselsdato=" + fodselsdato + ", adresse=" + adresse
		        + ", navn=" + navn + ", barn=" + barn + "]";
	}

}
