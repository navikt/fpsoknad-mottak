package no.nav.foreldrepenger.oppslag.inntekt;

import no.nav.foreldrepenger.oppslag.domain.Inntekt;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;

import java.util.Optional;

final class InntektMapper {

    public static Inntekt map(no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt inntekt) {
        return new Inntekt(inntekt.getBeloep().doubleValue(),
                CalendarConverter.toLocalDate(inntekt.getOpptjeningsperiode().getStartDato()),
                Optional.of(CalendarConverter.toLocalDate(inntekt.getOpptjeningsperiode().getSluttDato())));
    }

}
