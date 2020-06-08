package no.nav.foreldrepenger.mottak.innsending.pdf.modell;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class ListeBlokk extends Blokk {
    private String tittel;
    private List<String> punkter;

    public ListeBlokk(String tittel, String... punkter) {
        this(tittel, Arrays.asList(punkter));
    }
}
