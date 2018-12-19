package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.UKJENT;

import java.io.StringReader;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;

@Component
public final class DefaultSøknadInspektør implements SøknadInspektør {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSøknadInspektør.class);

    @Override
    public Versjon versjon(String xml) {
        return versjonFra(xml);
    }

    @Override
    public SøknadType type(no.nav.vedtak.felles.xml.soeknad.v1.Soeknad søknad) {
        return typeFra(søknad);
    }

    @Override
    public SøknadType type(no.nav.vedtak.felles.xml.soeknad.v2.Soeknad søknad) {
        return typeFra(søknad);

    }

    private SøknadType typeFra(no.nav.vedtak.felles.xml.soeknad.v2.Soeknad søknad) {
        no.nav.vedtak.felles.xml.soeknad.v2.OmYtelse omYtelse = søknad.getOmYtelse();
        if (omYtelse == null || omYtelse.getAny() == null || omYtelse.getAny().isEmpty()) {
            LOG.warn("Ingen ytelse i søknaden");
            return UKJENT;
        }
        if (omYtelse.getAny().size() > 1) {
            LOG.warn("Fikk {} ytelser i søknaden, forventet  1, behandler kun den første", omYtelse.getAny().size());
        }
        Object førsteYtelse = ((JAXBElement<?>) omYtelse.getAny().get(0)).getValue();
        if (førsteYtelse instanceof no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.Endringssoeknad) {
            return ENDRING;
        }

        if (førsteYtelse instanceof no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.Foreldrepenger) {
            return INITIELL;
        }
        return UKJENT;
    }

    private static SøknadType typeFra(no.nav.vedtak.felles.xml.soeknad.v1.Soeknad søknad) {
        no.nav.vedtak.felles.xml.soeknad.v1.OmYtelse omYtelse = søknad.getOmYtelse();
        if (omYtelse == null || omYtelse.getAny() == null || omYtelse.getAny().isEmpty()) {
            LOG.warn("Ingen ytelse i søknaden");
            return UKJENT;
        }
        if (omYtelse.getAny().size() > 1) {
            LOG.warn("Fikk {} ytelser i søknaden, forventet  1, behandler kun den første", omYtelse.getAny().size());
        }
        Object førsteYtelse = ((JAXBElement<?>) omYtelse.getAny().get(0)).getValue();
        if (førsteYtelse instanceof no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.Endringssoeknad) {
            return ENDRING;
        }

        if (førsteYtelse instanceof no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger) {
            return INITIELL;
        }
        return UKJENT;

    }

    private static Versjon versjonFra(String xml) {
        try {
            XMLStreamReader reader = XMLInputFactory.newInstance()
                    .createXMLStreamReader(new StreamSource(new StringReader(xml)));
            while (!reader.isStartElement()) {
                reader.next();
            }
            return Versjon.fraNamespace(reader.getNamespaceURI());
        } catch (XMLStreamException e) {
            throw new IllegalStateException(e);
        }
    }
}
