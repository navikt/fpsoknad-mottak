package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
public record DokumentBestilling(
        String dokument,
        DokumentPerson søker,
        MottattDato mottattDato,
        List<TemaBlokk> temaer) {
}
