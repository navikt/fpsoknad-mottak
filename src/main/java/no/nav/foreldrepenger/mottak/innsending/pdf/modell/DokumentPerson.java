package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "med")
public class DokumentPerson {
    String id;
    String typeId;
    String navn;
    PersonType type;
    String bosattLand;
    String nasjonalitet;

    public static DokumentPerson ukjentPerson = builder().medType(PersonType.UKJENT).build();
}
