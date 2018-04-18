package no.nav.foreldrepenger.oppslag.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.neovisionaries.i18n.CountryCode;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({ "id", "fodselsdato", "navn", "kjonn", "målform" })
public class Person {

    @JsonUnwrapped
    private final ID id;
    private final CountryCode landKode;
    private final Kjonn kjonn;
    private final LocalDate fodselsdato;
    private final String målform;
    private final Bankkonto bankkonto;
    @JsonUnwrapped
    private final Navn navn;
    private final List<Barn> barn;

    public Person(ID id, CountryCode landKode, Kjonn kjonn, Navn navn, String målform,
                  Bankkonto bankkonto, LocalDate fodselsdato, List<Barn> barn) {
        this.id = id;
        this.landKode = landKode;
        this.kjonn = kjonn;
        this.målform = målform;
        this.bankkonto = bankkonto;
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

    public CountryCode getLandKode() {
        return landKode;
    }

    public List<Barn> getBarn() {
        return barn;
    }

    public String getMålform() {
        return målform;
    }

    public Bankkonto getBankkonto() {
        return bankkonto;
    }

    public Navn getNavn() {
        return navn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                landKode == person.landKode &&
                kjonn == person.kjonn &&
                Objects.equals(fodselsdato, person.fodselsdato) &&
                Objects.equals(målform, person.målform) &&
                Objects.equals(bankkonto, person.bankkonto) &&
                Objects.equals(navn, person.navn) &&
                Objects.equals(landKode, person.landKode) &
                        Objects.equals(barn, person.barn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, landKode, kjonn, fodselsdato, målform, bankkonto, navn, barn);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [id=" + id + ", landKode=" + landKode + ", kjonn=" + kjonn
                + ", fodselsdato=" + fodselsdato
                + ", målform=" + målform + ", bankkonto=" + bankkonto
                + ", navn=" + navn + ", barn=" + barn + "]";
    }
}
