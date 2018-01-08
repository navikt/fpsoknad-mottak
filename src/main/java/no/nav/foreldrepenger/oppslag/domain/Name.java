package no.nav.foreldrepenger.oppslag.domain;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Name {

	private String fornavn;
	private String mellomnavn;
	private String etternavn;

	@JsonCreator
	public Name(@JsonProperty("fornavn") String fornavn, @JsonProperty("mellomnavn") String mellomnavn,
	        @JsonProperty("etternavn") String etternavn) {
		this.fornavn = fornavn;
		this.mellomnavn = mellomnavn;
		this.etternavn = etternavn;
	}

	public Name(String fornavn, String etternavn) {
		this(fornavn, null, etternavn);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fornavn, mellomnavn, etternavn);
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
		Name other = (Name) obj;
		if (etternavn == null) {
			if (other.etternavn != null) {
				return false;
			}
		} else if (!etternavn.equals(other.etternavn)) {
			return false;
		}
		if (fornavn == null) {
			if (other.fornavn != null) {
				return false;
			}
		} else if (!fornavn.equals(other.fornavn)) {
			return false;
		}
		if (mellomnavn == null) {
			if (other.mellomnavn != null) {
				return false;
			}
		} else if (!mellomnavn.equals(other.mellomnavn)) {
			return false;
		}
		return true;
	}

	public String getFornavn() {
		return fornavn;
	}

	public void setFornavn(String fornavn) {
		this.fornavn = fornavn;
	}

	public String getMellomnavn() {
		return mellomnavn;
	}

	public void setMellomnavn(String mellomnavn) {
		this.mellomnavn = mellomnavn;
	}

	public String getEtternavn() {
		return etternavn;
	}

	public void setEtternavn(String etternavn) {
		this.etternavn = etternavn;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [fornavn=" + fornavn + ", mellomnavn=" + mellomnavn + ", etternavn="
		        + etternavn + "]";
	}

}
