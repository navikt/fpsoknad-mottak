package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DokumentPerson {
    String id;
    String typeId;
    String navn;
    PersonType type;
    String bosattLand;
    String nasjonalitet;

    public static final DokumentPerson UKJENT = builder().type(PersonType.UKJENT).build();
}
