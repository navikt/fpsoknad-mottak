package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.function.Predicate.not;
import static no.nav.foreldrepenger.common.util.StreamUtil.onlyElem;
import static no.nav.foreldrepenger.common.util.StringUtil.mask;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLAdresseBeskyttelse.PDLAdresseGradering.UGRADERT;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLForelderBarnRelasjon.PDLRelasjonsRolle.BARN;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

class PDLBarn {
    private static final Logger LOG = LoggerFactory.getLogger(PDLBarn.class);
    @JsonProperty("foedsel")
    private final Set<PDLFødsel> fødselsdato;
    private final Set<PDLForelderBarnRelasjon> forelderBarnRelasjon;

    private final Set<PDLNavn> navn;
    @JsonProperty("kjoenn")
    private final Set<PDLKjønn> kjønn;
    @JsonProperty("adressebeskyttelse")
    private final Set<PDLAdresseBeskyttelse> beskyttelse;
    @JsonProperty("doedsfall")
    private final Set<PDLDødsfall> dødsfall;

    private PDLAnnenPart annenPart;
    private String id;

    public PDLBarn(Set<PDLFødsel> fødselsdato, Set<PDLForelderBarnRelasjon> forelderBarnRelasjon, Set<PDLNavn> navn, Set<PDLKjønn> kjønn, Set<PDLAdresseBeskyttelse> beskyttelse, Set<PDLDødsfall> dødsfall) {
        this.fødselsdato = fødselsdato;
        this.forelderBarnRelasjon = forelderBarnRelasjon;
        this.navn = navn;
        this.kjønn = kjønn;
        this.beskyttelse = beskyttelse;
        this.dødsfall = dødsfall;
    }

    public Set<PDLFødsel> getFødselsdato() {
        return fødselsdato;
    }

    public Set<PDLForelderBarnRelasjon> getForelderBarnRelasjon() {
        return forelderBarnRelasjon;
    }

    public Set<PDLNavn> getNavn() {
        return navn;
    }

    public Set<PDLKjønn> getKjønn() {
        return kjønn;
    }

    public Set<PDLAdresseBeskyttelse> getBeskyttelse() {
        return beskyttelse;
    }

    public Set<PDLDødsfall> getDødsfall() {
        return dødsfall;
    }

    public PDLAnnenPart getAnnenPart() {
        return annenPart;
    }

    String annenPart(String fnrSøker) {
        return forelderBarnRelasjon.stream()
                .filter(r -> r.minRolle().equals(BARN))
                .filter(not(r -> Objects.equals(r.id(), fnrSøker)))
                .findFirst()
                .map(PDLForelderBarnRelasjon::id)
                .orElse(null);
    }

    PDLBarn withId(String id) {
        this.id = id;
        return this;
    }

    PDLBarn withAnnenPart(PDLAnnenPart annenPart) {
        this.annenPart = annenPart;
        return this;
    }

    String getId() {
        return id;
    }

    boolean erNyligFødt(int måneder) {
        var dato = onlyElem(getFødselsdato()).fødselsdato();
        var nylig = dato.isAfter(LocalDate.now().minusMonths(måneder));
        LOG.info("Barn er {}født for mindre enn {} måneder siden ({})", nylig ? "" : "IKKE ", måneder, dato);
        return nylig;

    }

    boolean erBeskyttet() {
        var b = onlyElem(getBeskyttelse());
        var beskyttet = b != null ? !UGRADERT.equals(b.gradering()) : false;
        LOG.info("Barn er {}beskyttet", beskyttet ? "" : "IKKE ");
        return beskyttet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var pdlBarn = (PDLBarn) o;
        return Objects.equals(fødselsdato, pdlBarn.fødselsdato) && Objects.equals(forelderBarnRelasjon, pdlBarn.forelderBarnRelasjon) && Objects.equals(navn, pdlBarn.navn) && Objects.equals(kjønn, pdlBarn.kjønn) && Objects.equals(beskyttelse, pdlBarn.beskyttelse) && Objects.equals(dødsfall, pdlBarn.dødsfall) && Objects.equals(annenPart, pdlBarn.annenPart) && Objects.equals(id, pdlBarn.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fødselsdato, forelderBarnRelasjon, navn, kjønn, beskyttelse, dødsfall, annenPart, id);
    }

    @Override
    public String toString() {
        return "PDLBarn{" +
            "fødselsdato=" + fødselsdato +
            ", forelderBarnRelasjon=" + forelderBarnRelasjon +
            ", navn=" + navn +
            ", kjønn=" + kjønn +
            ", beskyttelse=" + beskyttelse +
            ", dødsfall=" + dødsfall +
            ", annenPart=" + annenPart +
            ", id='" + mask(id) + '\'' +
            '}';
    }


}
