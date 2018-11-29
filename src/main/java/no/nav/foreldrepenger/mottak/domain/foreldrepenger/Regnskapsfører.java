package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class Regnskapsfører {
    @Length(max = 100)
    private final String navn;
    private final String telefon;
}
