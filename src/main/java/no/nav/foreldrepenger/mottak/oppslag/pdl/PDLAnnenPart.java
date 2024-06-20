package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.common.util.StreamUtil.onlyElem;
import static no.nav.foreldrepenger.common.util.StringUtil.mask;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLAdresseBeskyttelse.PDLAdresseGradering.UGRADERT;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

class PDLAnnenPart {

    private static final Logger LOG = LoggerFactory.getLogger(PDLAnnenPart.class);

    private final Set<PDLNavn> navn;
    @JsonProperty("foedselsdato")
    private final Set<PDLFødsel> fødselsdato;
    @JsonProperty("kjoenn")
    private final Set<PDLKjønn> kjønn;
    @JsonProperty("doedsfall")
    private final Set<PDLDødsfall> dødsfall;
    @JsonProperty("adressebeskyttelse")
    private final Set<PDLAdresseBeskyttelse> beskyttelse;

    private String id;

    public PDLAnnenPart(Set<PDLNavn> navn, Set<PDLFødsel> fødselsdato, Set<PDLKjønn> kjønn, Set<PDLDødsfall> dødsfall, Set<PDLAdresseBeskyttelse> beskyttelse) {
        this.navn = navn;
        this.fødselsdato = fødselsdato;
        this.kjønn = kjønn;
        this.dødsfall = dødsfall;
        this.beskyttelse = beskyttelse;
    }

    public Set<PDLNavn> getNavn() {
        return navn;
    }

    public Set<PDLFødsel> getFødselsdato() {
        return fødselsdato;
    }

    public Set<PDLKjønn> getKjønn() {
        return kjønn;
    }

    public Set<PDLDødsfall> getDødsfall() {
        return dødsfall;
    }

    public Set<PDLAdresseBeskyttelse> getBeskyttelse() {
        return beskyttelse;
    }

    public String getId() {
        return id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (PDLAnnenPart) o;
        return Objects.equals(navn, that.navn) && Objects.equals(fødselsdato, that.fødselsdato) && Objects.equals(kjønn, that.kjønn) && Objects.equals(dødsfall, that.dødsfall) && Objects.equals(beskyttelse, that.beskyttelse) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(navn, fødselsdato, kjønn, dødsfall, beskyttelse, id);
    }

    @Override
    public String
    toString() {
        return "PDLAnnenPart{" +
            "navn=" + navn +
            ", fødselsdato=" + fødselsdato +
            ", kjønn=" + kjønn +
            ", dødsfall=" + dødsfall +
            ", beskyttelse=" + beskyttelse +
            ", id='" + mask(id) + '\'' +
            '}';
    }
}
