package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLAdresseBeskyttelse.PDLAdresseGradering.UGRADERT;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.onlyElem;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class PDLAnnenPart {

    private static final Logger LOG = LoggerFactory.getLogger(PDLAnnenPart.class);

    private final Set<PDLNavn> navn;
    private final Set<PDLFødsel> fødselsdato;
    private final Set<PDLKjønn> kjønn;
    private final Set<PDLDødsfall> dødsfall;
    private final Set<PDLAdresseBeskyttelse> beskyttelse;

    private String id;

    @JsonCreator
    PDLAnnenPart(@JsonProperty("navn") Set<PDLNavn> navn, @JsonProperty("foedsel") Set<PDLFødsel> fødselsdato,
            @JsonProperty("kjoenn") Set<PDLKjønn> kjønn, @JsonProperty("doedsfall") Set<PDLDødsfall> dødsfall,
            @JsonProperty("adressebeskyttelse") Set<PDLAdresseBeskyttelse> beskyttelse) {

        this.navn = navn;
        this.fødselsdato = fødselsdato;
        this.kjønn = kjønn;
        this.dødsfall = dødsfall;
        this.beskyttelse = beskyttelse;
    }

    PDLAnnenPart withId(String id) {
        this.id = id;
        return this;
    }

    boolean erDød() {
        return !dødsfall.isEmpty();
    }

    boolean erBeskyttet() {
        var b = onlyElem(getBeskyttelse());
        var beskyttet = b != null ? !UGRADERT.equals(b.gradering()) : false;
        LOG.info("Annen part er {} beskyttet", beskyttet ? "" : "IKKE");
        return beskyttet;
    }
}