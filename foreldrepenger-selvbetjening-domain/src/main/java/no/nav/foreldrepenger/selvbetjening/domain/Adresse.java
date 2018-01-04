package no.nav.foreldrepenger.selvbetjening.domain;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Adresse {

	private final String landkode;
	private final String poststed;
	private final String gatenavn;
	private final String bolignummer;
	private final String husbokstav;

	@JsonCreator
	public Adresse(@JsonProperty("landkode") String landkode, @JsonProperty("poststed") String poststed,
	        @JsonProperty("gatenavn") String gatenavn, @JsonProperty("bolignummer") String bolignummer,
	        @JsonProperty("husbokstav") String husbokstav) {
		this.landkode = landkode;
		this.poststed = poststed;
		this.gatenavn = gatenavn;
		this.bolignummer = bolignummer;
		this.husbokstav = husbokstav;
	}

	public String getHusbokstav() {
		return husbokstav;
	}

	public String getBolignummer() {
		return bolignummer;
	}

	public String getLandkode() {
		return landkode;
	}

	public String getPoststed() {
		return poststed;
	}

	public String getGatenavn() {
		return gatenavn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(landkode, poststed, gatenavn, bolignummer, husbokstav);
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
		Adresse other = (Adresse) obj;
		if (bolignummer == null) {
			if (other.bolignummer != null) {
				return false;
			}
		} else if (!bolignummer.equals(other.bolignummer)) {
			return false;
		}
		if (gatenavn == null) {
			if (other.gatenavn != null) {
				return false;
			}
		} else if (!gatenavn.equals(other.gatenavn)) {
			return false;
		}
		if (husbokstav == null) {
			if (other.husbokstav != null) {
				return false;
			}
		} else if (!husbokstav.equals(other.husbokstav)) {
			return false;
		}
		if (landkode == null) {
			if (other.landkode != null) {
				return false;
			}
		} else if (!landkode.equals(other.landkode)) {
			return false;
		}
		if (poststed == null) {
			if (other.poststed != null) {
				return false;
			}
		} else if (!poststed.equals(other.poststed)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [landkode=" + landkode + ", poststed=" + poststed + ", gatenavn="
		        + gatenavn + ", bolignummer=" + bolignummer + ", husbokstav=" + husbokstav + "]";
	}

}
