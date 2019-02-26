package no.nav.foreldrepenger.mottak.innsending.varsel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.util.jaxb.VarselJaxbUtil;
import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.Parameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varslingstyper;

@Service
public class VarselXMLGenerator {
    private final VarselJaxbUtil jaxb;
    static final String VARSEL_TYPE = "ForeldrepengerSoknadsvarsel";
    static final String FORNAVN = "FORNAVN";
    static final String DATO = "DATO";

    static final String URL = "URL";
    static final String URL_VALUE = "https://foreldrepenger.nav.no";

    private static final ObjectFactory VARSEL_FACTORY_V1 = new ObjectFactory();

    @Inject
    public VarselXMLGenerator() {
        this(true);
    }

    public VarselXMLGenerator(boolean validate) {
        this(new VarselJaxbUtil(validate));
    }

    public VarselXMLGenerator(VarselJaxbUtil jaxb) {
        this.jaxb = jaxb;
    }

    public String tilXml(no.nav.foreldrepenger.mottak.innsending.varsel.Varsel varsel) {
        return jaxb.marshal(VARSEL_FACTORY_V1.createVarsel(tilVarselModel(varsel)));
    }

    private static Varsel tilVarselModel(no.nav.foreldrepenger.mottak.innsending.varsel.Varsel varsel) {

        return new Varsel()
                .withMottaker(mottaker(varsel.getSøker()))
                .withVarslingstype(varslingsType())
                .withParameterListe(parameterListe(varsel.getSøker(), varsel.getDato()));
    }

    private static AktoerId mottaker(Person søker) {
        return new AktoerId()
                .withAktoerId(søker.aktørId.getId());
    }

    private static Varslingstyper varslingsType() {
        return new Varslingstyper()
                .withValue(VARSEL_TYPE);
    }

    private static List<Parameter> parameterListe(Person søker, LocalDateTime mottatt) {
        return Arrays.asList(
                new Parameter()
                        .withKey(FORNAVN)
                        .withValue(formattertNavn(søker.fornavn)),
                new Parameter()
                        .withKey(DATO)
                        .withValue(formattertDato(mottatt)),
                new Parameter()
                        .withKey(URL)
                        .withValue(URL_VALUE));
    }

    static String formattertNavn(String name) {
        return Optional.ofNullable(name)
            .map(n -> n.toLowerCase())
            .map(n -> Character.toUpperCase(n.charAt(0)) + n.substring(1))
            .orElse("");
    }

    static String formattertDato(LocalDateTime date) {
        return date.format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG)
                .withLocale(Locale.forLanguageTag("no"))
                .withZone(ZoneId.systemDefault()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [jaxb=" + jaxb + "]";
    }
}
