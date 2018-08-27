package no.nav.foreldrepenger.lookup.ws.inntekt;

import java.util.Optional;

import no.nav.foreldrepenger.time.DateUtil;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Aktoer;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.AktoerId;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Organisasjon;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.PersonIdent;

final class InntektMapper {

    private InntektMapper() {

    }

    public static Inntekt map(no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt inntekt) {
        return new Inntekt(
                Optional.ofNullable(inntekt.getOpptjeningsperiode())
                        .map(p -> DateUtil.toLocalDate(p.getStartDato())).orElse(null),
                Optional.ofNullable(inntekt.getOpptjeningsperiode())
                        .map(p -> DateUtil.toLocalDate(p.getSluttDato())),
                Optional.ofNullable(inntekt.getBeloep()).map(b -> b.doubleValue()).orElse(0.0),
                employerID(inntekt.getVirksomhet()));
    }

    private static String employerID(Aktoer aktoer) {
        if (aktoer instanceof PersonIdent) {
            return PersonIdent.class.cast(aktoer).getPersonIdent();
        }
        if (aktoer instanceof Organisasjon) {
            return Organisasjon.class.cast(aktoer).getOrgnummer();
        }
        return AktoerId.class.cast(aktoer).getAktoerId();
    }
}
