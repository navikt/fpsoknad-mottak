package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto;

import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;

public record ArbeidsavtaleDTO(Periode gyldighetsperiode, ProsentAndel stillingsprosent) {
}
