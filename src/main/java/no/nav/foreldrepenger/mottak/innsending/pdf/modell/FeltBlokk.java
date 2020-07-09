package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder(setterPrefix = "med")
@AllArgsConstructor
public class FeltBlokk extends Blokk {
    private String felt;
    private String verdi;

    public static FeltBlokk felt(String felt, String verdi) {
        return new FeltBlokk(felt, verdi);
    }
}
