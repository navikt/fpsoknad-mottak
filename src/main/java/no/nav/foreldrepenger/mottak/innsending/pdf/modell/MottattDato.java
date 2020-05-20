package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class MottattDato {
    private String beskrivelse;
    private String datoTid;
}
