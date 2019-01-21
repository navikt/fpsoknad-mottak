package no.nav.foreldrepenger.mottak.innsending.varsel;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.util.jaxb.VarselJaxbUtil;
import no.nav.melding.virksomhet.varsel.v1.varsel.*;
import org.springframework.stereotype.Service;

@Service
public class VarselXMLGenerator {
    private static final VarselJaxbUtil JAXB = new VarselJaxbUtil();
    private static final String VARSEL_TYPE = "FIKSFAKS";

    public static String tilXml(Person person) {
        return JAXB.marshal(tilVarselModel(person));
    }

    private static Varsel tilVarselModel(Person person) {
        return new Varsel()
            .withMottaker(new AktoerId().withAktoerId(person.aktørId.getId()))
            .withVarslingstype(new Varslingstyper().withValue(VARSEL_TYPE))
            .withParameterListe(new Parameter()
                .withKey("NAVN")
                .withValue(person.fornavn));
    }

}
