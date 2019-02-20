package no.nav.foreldrepenger.mottak.innsending.varsel;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.util.jaxb.VarselJaxbUtil;
import no.nav.melding.virksomhet.varsel.v1.varsel.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Locale;

@Service
public class VarselXMLGenerator {
    private static final VarselJaxbUtil JAXB = new VarselJaxbUtil();
    private static final String VARSEL_TYPE = "ForeldrepengerSoknadsvarsel"; //appconfig
    private static final String URL_FP_VERDI = "https://foreldrepenger.nav.no"; // dittnav.link ressurs i fasit?
    private static final String FORNAVN = "FORNAVN";
    private static final String DATO = "DATO";
    private static final String TID = "TID";
    private static final String URL_FP = "URL_FP";

    public static String tilXml(Person person, LocalDateTime mottatt) {
        return JAXB.marshal(tilVarselModel(person, mottatt));
    }

    private static Varsel tilVarselModel(Person person, LocalDateTime mottatt) {
        String tidFormattert = mottatt.format(DateTimeFormatter.ofPattern("HH:mm"));

        return new Varsel()
            .withMottaker(new AktoerId().withAktoerId(person.aktørId.getId()))
            .withVarslingstype(new Varslingstyper().withValue(VARSEL_TYPE))
            .withParameterListe(
                Arrays.asList(
                    new Parameter()
                        .withKey(FORNAVN)
                        .withValue(person.fornavn),
                    new Parameter()
                        .withKey(DATO)
                        .withValue(formattertDato(mottatt)),
                    new Parameter()
                        .withKey(TID)
                        .withValue(tidFormattert),
                    new Parameter()
                        .withKey(URL_FP)
                        .withValue(URL_FP_VERDI)));
    }

    private static String formattertDato(LocalDateTime date) {
        return date.format(DateTimeFormatter
            .ofLocalizedDate(FormatStyle.LONG)
            .withLocale(Locale.forLanguageTag("no"))
            .withZone(ZoneId.systemDefault()));
    }

}
