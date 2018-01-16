package no.nav.foreldrepenger.oppslag.domain;

import java.util.Objects;

public class Navn {

	private final String fornavn;
	private final String mellomnavn;
	private final String etternavn;

	public Navn(String fornavn, String mellomnavn, String etternavn) {
		this.fornavn = fornavn;
		this.mellomnavn = mellomnavn;
		this.etternavn = etternavn;
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
		Navn other = (Navn) obj;
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

	public String getMellomnavn() {
		return mellomnavn;
	}

	public String getEtternavn() {
		return etternavn;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [fornavn=" + fornavn + ", mellomnavn=" + mellomnavn + ", etternavn="
		        + etternavn + "]";
	}

}
