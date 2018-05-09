package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.Navn;

@Data
public class Regnskapsfører {
    private final Navn navn;
    private final String telefon;
}
