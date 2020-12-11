package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
public class DokumentBestilling {
    private String dokument;
    private DokumentPerson søker;
    private MottattDato mottattDato;
    private List<TemaBlokk> temaer;
}
