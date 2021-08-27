package no.nav.foreldrepenger.mottak.innsending.varsel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.util.jaxb.V1VarselJAXBUtil;
import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.Parameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varslingstyper;

@Service
class VarselXMLGenerator {
    private static final ObjectFactory VARSEL_FACTORY_V1 = new ObjectFactory();
    static final String VARSEL_TYPE = "ForeldrepengerSoknadsvarsel";
    static final String FORNAVN = "FORNAVN";
    static final String DATO = "DATO";
    static final String URL = "URL";
    static final String URL_VALUE = "https://foreldrepenger.nav.no";
    private final V1VarselJAXBUtil jaxb;

    public VarselXMLGenerator() {
        this(true);
    }

    private VarselXMLGenerator(boolean validate) {
        this(new V1VarselJAXBUtil(validate));
    }

    VarselXMLGenerator(V1VarselJAXBUtil jaxb) {
        this.jaxb = jaxb;
    }

    public String tilXml(no.nav.foreldrepenger.mottak.innsending.varsel.Varsel varsel) {
        return jaxb.marshal(VARSEL_FACTORY_V1.createVarsel(tilVarselModel(varsel)));
    }

    private static Varsel tilVarselModel(no.nav.foreldrepenger.mottak.innsending.varsel.Varsel varsel) {
        return new Varsel()
                .withMottaker(mottaker(varsel.søker()))
                .withVarslingstype(varslingsType())
                .withParameterListe(parameterListe(varsel));
    }

    private static AktoerId mottaker(Person søker) {
        return new AktoerId()
                .withAktoerId(søker.getAktørId().getId());
    }

    private static Varslingstyper varslingsType() {
        return new Varslingstyper()
                .withValue(VARSEL_TYPE);
    }

    private static List<Parameter> parameterListe(no.nav.foreldrepenger.mottak.innsending.varsel.Varsel varsel) {
        return List.of(
                new Parameter()
                        .withKey(FORNAVN)
                        .withValue(formattertNavn(varsel.søker().getFornavn())),
                new Parameter()
                        .withKey(DATO)
                        .withValue(formattertDato(varsel.dato())),
                new Parameter()
                        .withKey(URL)
                        .withValue(URL_VALUE));
    }

    static String formattertNavn(String name) {
        return Optional.ofNullable(name)
                .map(String::toLowerCase)
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
