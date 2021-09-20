package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.function.Predicate.not;
import static no.nav.foreldrepenger.common.util.StreamUtil.onlyElem;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLAdresseBeskyttelse.PDLAdresseGradering.UGRADERT;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLForelderBarnRelasjon.PDLRelasjonsRolle.BARN;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class PDLBarn {

    private static final Logger LOG = LoggerFactory.getLogger(PDLBarn.class);
    @JsonProperty("foedsel")
    private final Set<PDLFødsel> fødselsdato;
    private final Set<PDLForelderBarnRelasjon> forelderBarnRelasjon;
    private String id;
    private final Set<PDLNavn> navn;
    @JsonProperty("kjoenn")
    private final Set<PDLKjønn> kjønn;
    @JsonProperty("adressebeskyttelse")
    private final Set<PDLAdresseBeskyttelse> beskyttelse;
    @JsonProperty("doedsfall")
    private final Set<PDLDødsfall> dødsfall;

    private PDLAnnenPart annenPart;

    String annenPart(String fnrSøker) {
        return forelderBarnRelasjon.stream()
                .filter(r -> r.minRolle().equals(BARN))
                .filter(not(r -> r.id().equals(fnrSøker)))
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

    boolean erNyligDød(int måneder) {
        var nylig = safeStream(getDødsfall())
                .map(PDLDødsfall::dødsdato)
                .filter(Objects::nonNull)
                .anyMatch(d -> d.isAfter(LocalDate.now().minusMonths(måneder)));

        LOG.info("Barn er {} nylig dødt", nylig ? "" : "IKKE");
        return nylig;
    }

    boolean erNyligFødt(int måneder) {
        var dato = onlyElem(getFødselsdato()).fødselsdato();
        var nylig = dato.isAfter(LocalDate.now().minusMonths(måneder));
        LOG.info("Barn er {} født for mindre enn {} måneder siden ({})", nylig ? "" : "IKKE", måneder, dato);
        return nylig;

    }

    boolean erBeskyttet() {
        var b = onlyElem(getBeskyttelse());
        var beskyttet = b != null ? !UGRADERT.equals(b.gradering()) : false;
        LOG.info("Barn er {} beskyttet", beskyttet ? "" : "IKKE");
        return beskyttet;
    }
}
