package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.Set;

import lombok.Data;

@Data
class PDLSøker {
    private final Set<PDLNavn> navn;
    private final Set<PDLKjønn> kjønn;
    private final Set<PDLStatsborgerskap> statsborgerskap;
    private final Set<PDLFødsel> fødselsdato;
    private final Set<PDLFamilierelasjon> familierelasjoner;
    private final Set<PDLSivilstand> sivilstand;
    private String id;

    PDLSøker withId(String id) {
        this.id = id;
        return this;
    }
}
