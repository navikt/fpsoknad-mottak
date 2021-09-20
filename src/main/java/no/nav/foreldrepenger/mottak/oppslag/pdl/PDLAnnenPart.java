package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.common.util.StreamUtil.onlyElem;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLAdresseBeskyttelse.PDLAdresseGradering.UGRADERT;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class PDLAnnenPart {

    private static final Logger LOG = LoggerFactory.getLogger(PDLAnnenPart.class);

    private final Set<PDLNavn> navn;
    @JsonProperty("foedsel")
    private final Set<PDLFødsel> fødselsdato;
    @JsonProperty("kjoenn")
    private final Set<PDLKjønn> kjønn;
    @JsonProperty("doedsfall")
    private final Set<PDLDødsfall> dødsfall;
    @JsonProperty("adressebeskyttelse")
    private final Set<PDLAdresseBeskyttelse> beskyttelse;

    private String id;

    PDLAnnenPart withId(String id) {
        this.id = id;
        return this;
    }

    boolean erDød() {
        return !isEmpty(dødsfall);
    }

    boolean erBeskyttet() {
        var b = onlyElem(getBeskyttelse());
        var beskyttet = b != null ? !UGRADERT.equals(b.gradering()) : false;
        LOG.info("Annen part er {} beskyttet", beskyttet ? "" : "IKKE");
        return beskyttet;
    }
}
