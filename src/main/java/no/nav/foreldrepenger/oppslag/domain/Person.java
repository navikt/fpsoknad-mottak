package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.neovisionaries.i18n.CountryCode;

@JsonPropertyOrder({ "id", "fodselsdaato", "navn", "kjonn", "adresse" })
public class Person {

    @JsonUnwrapped
    private final ID id;
    private final CountryCode landKode;
    private final Kjonn kjonn;
    private final LocalDate fodselsdato;
    private final Adresse adresse;
    @JsonUnwrapped
    private final Navn navn;
    private final List<Barn> barn;

    public Person(ID id, CountryCode landKode, Kjonn kjonn, Navn navn, Adresse adresse, LocalDate fodselsdato,
            List<Barn> barn) {
        this.id = id;
        this.landKode = landKode;
        this.kjonn = kjonn;
        this.adresse = adresse;
        this.navn = navn;
        this.fodselsdato = fodselsdato;
        this.barn = barn;
    }

    public Kjonn getKjonn() {
        return kjonn;
    }

    public ID getId() {
        return id;
    }

    public LocalDate getFodselsdato() {
        return fodselsdato;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, landKode, kjonn, adresse, navn, fodselsdato, barn);
    }

    public CountryCode getLandKode() {
        return landKode;
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
        return Objects.equals(this.adresse, other.adresse) && Objects.equals(this.barn, other.barn)
                && Objects.equals(this.fodselsdato, other.fodselsdato) && Objects.equals(this.id, other.id)
                && Objects.equals(this.landKode, other.landKode)
                && Objects.equals(this.kjonn, other.kjonn) && Objects.equals(this.navn, other.navn);
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
