package no.nav.foreldrepenger.oppslag.http.lookup.aareg;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.oppslag.http.lookup.person.Pair;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Aktoer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsavtale;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.HistoriskArbeidsgiverMedArbeidsgivernummer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Person;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Yrker;

public class ArbeidsforholdMapper {

    public static Arbeidsforhold map(
            no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold forhold) {
        return new Arbeidsforhold(
                arbeidsgiver(forhold.getArbeidsgiver()).getFirst(),
                arbeidsgiver(forhold.getArbeidsgiver()).getSecond(),
                yrker(forhold.getArbeidsavtale()),
                CalendarConverter.toLocalDate(forhold.getAnsettelsesPeriode().getPeriode().getFom()),
                Optional.ofNullable(forhold.getAnsettelsesPeriode().getPeriode().getTom())
                        .map(CalendarConverter::toLocalDate));
    }

    private static String yrker(List<Arbeidsavtale> avtaler) {
        return avtaler.stream()
                .map(Arbeidsavtale::getYrke)
                .distinct()
                .map(Yrker::getValue)
                .collect(joining("/"));
    }

    private static Pair<String, String> arbeidsgiver(Aktoer aktor) {
        Pair<String, String> pair;
        if (aktor instanceof Organisasjon) {
            Organisasjon org = (Organisasjon) aktor;
            pair = org.getNavn() != null ? Pair.of(org.getNavn(), "navn") : Pair.of(org.getOrgnummer(), "orgnr");
        } else if (aktor instanceof HistoriskArbeidsgiverMedArbeidsgivernummer) {
            HistoriskArbeidsgiverMedArbeidsgivernummer h = (HistoriskArbeidsgiverMedArbeidsgivernummer) aktor;
            pair = h.getNavn() != null ? Pair.of(h.getNavn(), "navn")
                    : Pair.of(h.getArbeidsgivernummer(), "arbeidsgivernr");
        } else {
            Person person = (Person) aktor;
            pair = Pair.of(person.getIdent().getIdent(), "fnr");
        }

        return pair;
    }

}
