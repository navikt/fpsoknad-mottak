package no.nav.foreldrepenger.oppslag.domain;

import java.util.Objects;

import com.neovisionaries.i18n.CountryCode;

public class Adresse {

    private final CountryCode landkode;
    private final String postNummer;
    private final String gatenavn;
    private final String bolignummer;
    private final String husbokstav;

    public Adresse(CountryCode landkode, String postNummer, String gatenavn, String bolignummer, String husbokstav) {
        this.landkode = landkode;
        this.postNummer = postNummer;
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

    public CountryCode getLandkode() {
        return landkode;
    }

    public String getPostNummer() {
        return postNummer;
    }

    public String getGatenavn() {
        return gatenavn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(landkode, postNummer, gatenavn, bolignummer, husbokstav);
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
        return Objects.equals(landkode, that.landkode) && Objects.equals(postNummer, that.postNummer)
                && Objects.equals(gatenavn, that.gatenavn) && Objects.equals(bolignummer, that.bolignummer)
                && Objects.equals(husbokstav, that.husbokstav);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [landkode=" + landkode + ", postNummer=" + postNummer + ", gatenavn="
                + gatenavn + ", bolignummer=" + bolignummer + ", husbokstav=" + husbokstav + "]";
    }

}
