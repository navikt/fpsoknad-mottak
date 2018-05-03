package no.nav.foreldrepenger.oppslag.lookup.ws.aareg;

import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.*;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;

import java.time.LocalDate;

public class TestdataProvider {

    public static Arbeidsforhold forhold(Aktoer aktoer) {
        Arbeidsforhold forhold = new Arbeidsforhold();

        forhold.setArbeidsgiver(aktoer);

        Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();
        LocalDate now = LocalDate.now();
        LocalDate earlier = now.minusMonths(2);
        gyldighetsperiode.setFom(CalendarConverter.toXMLGregorianCalendar(earlier));
        gyldighetsperiode.setTom(CalendarConverter.toXMLGregorianCalendar(now));

        AnsettelsesPeriode ansettelsesperiode = new AnsettelsesPeriode();
        ansettelsesperiode.setPeriode(gyldighetsperiode);
        forhold.setAnsettelsesPeriode(ansettelsesperiode);

        Arbeidsforholdstyper type = new Arbeidsforholdstyper();
        type.setValue("typen");
        forhold.setArbeidsforholdstype(type);

        Arbeidsavtale avtale = new Arbeidsavtale();
        Yrker yrker = new Yrker();
        yrker.setValue("yrke1");
        avtale.setYrke(yrker);
        forhold.getArbeidsavtale().add(avtale);

        avtale = new Arbeidsavtale();
        yrker = new Yrker();
        yrker.setValue("yrke2");
        avtale.setYrke(yrker);
        forhold.getArbeidsavtale().add(avtale);
        return forhold;
    }

    public static FinnArbeidsforholdPrArbeidstakerResponse response() {
        Organisasjon org = new Organisasjon();
        org.setOrgnummer("889640782");
        org.setNavn("NAV");
        FinnArbeidsforholdPrArbeidstakerResponse response = new FinnArbeidsforholdPrArbeidstakerResponse();
        response.getArbeidsforhold().add(forhold(org));
        return response;
    }

}
