package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class GruppeBlokk extends Blokk {
    private final String overskrift;
    private final String beskrivelse;
    private final List<? extends Blokk> underBlokker;
}
