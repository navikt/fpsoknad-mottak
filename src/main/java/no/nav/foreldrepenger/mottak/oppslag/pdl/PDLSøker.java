package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.common.util.StringUtil.mask;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

class PDLSøker {
    private final Set<PDLNavn> navn;
    @JsonProperty("kjoenn")
    private final Set<PDLKjønn> kjønn;
    private final Set<PDLStatsborgerskap> statsborgerskap;
    @JsonProperty("foedsel")
    private final Set<PDLFødsel> fødselsdato;
    private final Set<PDLForelderBarnRelasjon> forelderBarnRelasjon;
    @JsonProperty("doedfoedtBarn")
    private final List<PDLDødfødtBarn> dødfødtBarn;
    private final Set<PDLSivilstand> sivilstand;
    private String id;

    public PDLSøker(Set<PDLNavn> navn,
                    Set<PDLKjønn> kjønn,
                    Set<PDLStatsborgerskap> statsborgerskap,
                    Set<PDLFødsel> fødselsdato,
                    Set<PDLForelderBarnRelasjon> forelderBarnRelasjon,
                    List<PDLDødfødtBarn> dødfødtBarn,
                    Set<PDLSivilstand> sivilstand) {
        this.navn = navn;
        this.kjønn = kjønn;
        this.statsborgerskap = statsborgerskap;
        this.fødselsdato = fødselsdato;
        this.forelderBarnRelasjon = forelderBarnRelasjon;
        this.dødfødtBarn = dødfødtBarn;
        this.sivilstand = sivilstand;
    }

    public Set<PDLNavn> getNavn() {
        return navn;
    }

    public Set<PDLKjønn> getKjønn() {
        return kjønn;
    }

    public Set<PDLStatsborgerskap> getStatsborgerskap() {
        return statsborgerskap;
    }

    public Set<PDLFødsel> getFødselsdato() {
        return fødselsdato;
    }

    public Set<PDLForelderBarnRelasjon> getForelderBarnRelasjon() {
        return forelderBarnRelasjon;
    }

    public List<PDLDødfødtBarn> getDødfødtBarn() {
        return dødfødtBarn;
    }

    public Set<PDLSivilstand> getSivilstand() {
        return sivilstand;
    }

    public String getId() {
        return id;
    }

    PDLSøker withId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PDLSøker pdlSøker = (PDLSøker) o;
        return Objects.equals(navn, pdlSøker.navn) && Objects.equals(kjønn, pdlSøker.kjønn) && Objects.equals(statsborgerskap,
            pdlSøker.statsborgerskap) && Objects.equals(fødselsdato, pdlSøker.fødselsdato) && Objects.equals(forelderBarnRelasjon,
            pdlSøker.forelderBarnRelasjon) && Objects.equals(dødfødtBarn, pdlSøker.dødfødtBarn) && Objects.equals(id, pdlSøker.id)
            && Objects.equals(sivilstand, pdlSøker.sivilstand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(navn, kjønn, statsborgerskap, fødselsdato, forelderBarnRelasjon, dødfødtBarn, id, sivilstand);
    }

    @Override
    public String toString() {
        return "PDLSøker{" + "navn=" + navn + ", kjønn=" + kjønn + ", statsborgerskap=" + statsborgerskap + ", fødselsdato=" + fødselsdato
            + ", forelderBarnRelasjon=" + forelderBarnRelasjon + ", dødfødtBarn=" + dødfødtBarn + ", sivilstand=" + sivilstand + ", id='" + mask(id) + '\''
            + '}';
    }
}
