package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder(setterPrefix = "med")
@AllArgsConstructor
public class TemaBlokk extends Blokk {
    private final String overskrift;
    private final List<? extends Blokk> underBlokker;
}
