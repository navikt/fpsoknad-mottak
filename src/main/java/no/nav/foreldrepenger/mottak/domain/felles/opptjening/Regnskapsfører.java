package no.nav.foreldrepenger.mottak.domain.felles.opptjening;

import org.hibernate.validator.constraints.Length;

public record Regnskapsfører(@Length(max = 100) String navn, String telefon) {
}
