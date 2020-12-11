package no.nav.foreldrepenger.mottak.domain.felles.opptjening;

import org.hibernate.validator.constraints.Length;

import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;

public record FrilansOppdrag(@Length(max = 100) String oppdragsgiver, ÅpenPeriode periode) {

}
