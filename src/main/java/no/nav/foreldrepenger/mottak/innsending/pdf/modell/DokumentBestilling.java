package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class DokumentBestilling {
    private String dokument;
    private DokumentPerson søker;
    private MottattDato mottattDato;
    private List<TemaBlokk> temaer;
}
