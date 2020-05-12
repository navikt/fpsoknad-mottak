package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder(setterPrefix = "med")
@AllArgsConstructor
public class FeltBlokk extends Blokk {
    private String felt;
    private String verdi;
}
