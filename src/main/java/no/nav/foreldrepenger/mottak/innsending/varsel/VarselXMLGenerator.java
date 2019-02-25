package no.nav.foreldrepenger.mottak.innsending.varsel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.util.jaxb.VarselJaxbUtil;
import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.Parameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varslingstyper;

@Service
public class VarselXMLGenerator {
    private static final VarselJaxbUtil JAXB = new VarselJaxbUtil();
    private static final String VARSEL_TYPE = "ForeldrepengerSoknadsvarsel";
    private static final String FORNAVN = "FORNAVN";
    private static final String DATO = "DATO";

    private static final String URL_FP = "URL_FP";
    private static final String URL_FP_VALUE = "https://foreldrepenger.nav.no";

    public String tilXml(Person person, LocalDateTime mottatt) {
        return JAXB.marshal(tilVarselModel(person, mottatt));
    }

    private static Varsel tilVarselModel(Person søker, LocalDateTime mottatt) {

        return new Varsel()
                .withMottaker(mottaker(søker))
                .withVarslingstype(varslingsType())
                .withParameterListe(parameterListe(søker, mottatt));
    }

    private static AktoerId mottaker(Person søker) {
        return new AktoerId().withAktoerId(søker.aktørId.getId());
    }

    private static Varslingstyper varslingsType() {
        return new Varslingstyper().withValue(VARSEL_TYPE);
    }

    private static List<Parameter> parameterListe(Person søker, LocalDateTime mottatt) {
        return Arrays.asList(
                new Parameter()
                        .withKey(FORNAVN)
                        .withValue(søker.fornavn),
                new Parameter()
                        .withKey(DATO)
                        .withValue(formattertDato(mottatt)),
                new Parameter()
                        .withKey(URL_FP)
                        .withValue(URL_FP_VALUE));
    }

    private static String formattertDato(LocalDateTime date) {
        return date.format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG)
                .withLocale(Locale.forLanguageTag("no"))
                .withZone(ZoneId.systemDefault()));
    }
}
