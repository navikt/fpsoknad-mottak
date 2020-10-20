package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLAdresseGradering.UGRADERT;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.onlyElem;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLFamilierelasjon.PDLRelasjonsRolle;

@Data
class PDLBarn {
    private final Set<PDLFødsel> fødselsdato;
    private final Set<PDLFamilierelasjon> familierelasjoner;
    private String id;
    private final Set<PDLNavn> navn;
    private final Set<PDLKjønn> kjønn;
    private final Set<PDLAdresseBeskyttelse> beskyttelse;
    private final Set<PDLDødsfall> dødsfall;

    private PDLAnnenPart annenPart;

    @JsonCreator
    PDLBarn(@JsonProperty("foedsel") Set<PDLFødsel> fødselsdato,
            @JsonProperty("familierelasjoner") Set<PDLFamilierelasjon> familierelasjoner, @JsonProperty("navn") Set<PDLNavn> navn,
            @JsonProperty("kjoenn") Set<PDLKjønn> kjønn,
            @JsonProperty("doedsfall") Set<PDLDødsfall> dødsfall,
            @JsonProperty("adressebeskyttelse") Set<PDLAdresseBeskyttelse> beskyttelse) {
        this.fødselsdato = fødselsdato;
        this.familierelasjoner = familierelasjoner;
        this.navn = navn;
        this.kjønn = kjønn;
        this.beskyttelse = beskyttelse;
        this.dødsfall = dødsfall;
    }

    String annenPart(String fnrSøker) {
        return familierelasjoner.stream()
                .filter(r -> r.getMinRolle().equals(PDLRelasjonsRolle.BARN))
                .filter(r -> !r.getId().equals(fnrSøker))
                .findFirst()
                .map(p -> p.getId())
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

    boolean erNyligDød(int måneder) {
        return safeStream(getDødsfall())
                .map(PDLDødsfall::getDødsdato)
                .anyMatch(d -> d.isAfter(LocalDate.now().minusMonths(måneder)));
    }

    boolean erNyligFødt(int måneder) {
        return onlyElem(getFødselsdato()).getFødselsdato().isAfter(LocalDate.now().minusMonths(måneder));
    }

    boolean erBeskyttet() {
        return !onlyElem(getBeskyttelse()).getGradering().equals(UGRADERT);
    }
}