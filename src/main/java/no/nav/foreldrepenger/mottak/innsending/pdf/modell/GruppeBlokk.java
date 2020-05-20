package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder(setterPrefix = "med")
public class GruppeBlokk extends Blokk {
    private final String overskrift;
    private final String beskrivelse;
    private final List<? extends Blokk> underBlokker;
}
