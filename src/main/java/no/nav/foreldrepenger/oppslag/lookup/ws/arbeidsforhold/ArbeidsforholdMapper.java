package no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold;

import no.nav.foreldrepenger.oppslag.lookup.Pair;
import no.nav.foreldrepenger.oppslag.time.DateUtil;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDate.now;
import static no.nav.foreldrepenger.oppslag.time.DateUtil.dateWithinPeriod;
import static no.nav.foreldrepenger.oppslag.time.DateUtil.toLocalDate;

public class ArbeidsforholdMapper {

    public static Arbeidsforhold map(
            no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold forhold) {
        return new Arbeidsforhold(
                arbeidsgiverIdOgType(forhold.getArbeidsgiver()).getFirst(),
                arbeidsgiverIdOgType(forhold.getArbeidsgiver()).getSecond(),
                stillingsprosent(forhold.getArbeidsavtale()),
                toLocalDate(forhold.getAnsettelsesPeriode().getPeriode().getFom()),
                Optional.ofNullable(forhold.getAnsettelsesPeriode().getPeriode().getTom()).map(DateUtil::toLocalDate));
    }

    private static Pair<String, String> arbeidsgiverIdOgType(Aktoer aktor) {
        Pair<String, String> pair;
        if (aktor instanceof Organisasjon) {
            Organisasjon org = (Organisasjon) aktor;
            pair = Pair.of(org.getOrgnummer(), "orgnr");
        } else if (aktor instanceof HistoriskArbeidsgiverMedArbeidsgivernummer) {
            HistoriskArbeidsgiverMedArbeidsgivernummer h = (HistoriskArbeidsgiverMedArbeidsgivernummer) aktor;
            pair = Pair.of(h.getArbeidsgivernummer(), "arbeidsgivernr");
        } else {
            Person person = (Person) aktor;
            pair = Pair.of(person.getIdent().getIdent(), "fnr");
        }

        return pair;
    }

    private static Double stillingsprosent(List<Arbeidsavtale> avtaler) {
        return avtaler.stream()
            .filter(ArbeidsforholdMapper::gjeldendeAvtale)
            .map(Arbeidsavtale::getStillingsprosent)
            .map(BigDecimal::doubleValue)
            .findFirst().orElse(null);
    }

    private static boolean gjeldendeAvtale(Arbeidsavtale avtale) {
        LocalDate fom = toLocalDate(avtale.getFomGyldighetsperiode());
        LocalDate tom = toLocalDate(avtale.getTomGyldighetsperiode());

        return dateWithinPeriod(now(), fom, tom);
    }

}
