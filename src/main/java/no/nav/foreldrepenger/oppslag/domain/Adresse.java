package no.nav.foreldrepenger.oppslag.domain;

import java.util.Objects;

public class Adresse {

	private final String landkode;
	private final String poststed;
	private final String gatenavn;
	private final String bolignummer;
	private final String husbokstav;

	public Adresse(String landkode, String poststed, String gatenavn, String bolignummer, String husbokstav) {
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		Adresse that = (Adresse) o;
		return Objects.equals(landkode, that.landkode) && Objects.equals(poststed,that.poststed)
		        && Objects.equals(gatenavn, that.gatenavn) && Objects.equals(bolignummer, that.bolignummer)
		        && Objects.equals(husbokstav, that.husbokstav);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [landkode=" + landkode + ", poststed=" + poststed + ", gatenavn="
		        + gatenavn + ", bolignummer=" + bolignummer + ", husbokstav=" + husbokstav + "]";
	}

}
