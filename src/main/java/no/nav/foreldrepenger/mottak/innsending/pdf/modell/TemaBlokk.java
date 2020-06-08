package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder(setterPrefix = "med")
@AllArgsConstructor
public class TemaBlokk extends Blokk {
    private final String overskrift;
    private final List<? extends Blokk> underBlokker;
}
