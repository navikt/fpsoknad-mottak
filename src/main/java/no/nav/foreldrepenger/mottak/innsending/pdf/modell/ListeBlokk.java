package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
public class ListeBlokk extends Blokk {
    private String tittel;
    private List<String> punkter;

    public ListeBlokk(String tittel, String... punkter) {
        this(tittel, Arrays.asList(punkter));
    }
}
