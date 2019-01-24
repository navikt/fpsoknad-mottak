package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;

@Data
public class Inntektsmelding {

    private final Ytelse ytelse;
    private final InnsendingsÅrsak innsendingsÅrsak;
    private final Arbeidsgiver arbeidsgiver;
    private final Fødselsnummer fødselsnummer;
    private final boolean nærRelasjon;
    private final Arbeidsforhold arbeidsforhold;
    private final Refusjon refusjon;
    private final SykepengerIArbeidsgiverPerioden sykepengerIArbeidsgiverPerioden;
    private final LocalDate startdatoForeldrepenger;
    private final List<Naturalytelse> opphørAvNaturalytelser;
    private final List<Naturalytelse> gjenopptakelseAvNaturalytelser;
    private final Avsender avsender;
    private final List<LukketPeriode> pleiepengePerioder;
    private final OmsorgsPenger omsorgspenger;

}
