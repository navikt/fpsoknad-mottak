package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

@Component
public final class JAXBFPV1Helper extends AbstractJaxb {

    private static final Versjon VERSJON = V1;

    private static final Logger LOG = LoggerFactory.getLogger(JAXBFPV1Helper.class);

    public JAXBFPV1Helper() {
        super(contextFra(
                Endringssoeknad.class,
                Foreldrepenger.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.ObjectFactory.class,
                Soeknad.class),
                schema());
    }

    private static Schema schema() {
        try {
            return SCHEMA_FACTORY
                    .newSchema(sourcesFra(VERSJON,
                            "/foreldrepenger/foreldrepenger-v1.xsd",
                            "/endringssoeknad-v1.xsd",
                            "/soeknad-v1.xsd"));
        } catch (SAXException e) {
            LOG.warn("Noe gikk galt med konfigurasjon av validering, bruker ikke-validerende marshaller");
            return null;
        }
    }

    @Override
    public Versjon versjon() {
        return VERSJON;
    }
}
