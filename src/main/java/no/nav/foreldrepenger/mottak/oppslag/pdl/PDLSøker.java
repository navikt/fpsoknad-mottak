package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
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
    private String id;

    PDLSøker withId(String id) {
        this.id = id;
        return this;
    }
}
