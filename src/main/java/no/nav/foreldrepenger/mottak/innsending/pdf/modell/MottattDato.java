package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
public record MottattDato(String beskrivelse, String datoTid) {
}
