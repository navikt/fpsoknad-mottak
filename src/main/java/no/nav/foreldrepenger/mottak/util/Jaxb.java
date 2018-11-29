package no.nav.foreldrepenger.mottak.util;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;

public final class Jaxb {

    public enum ValidationMode {
        ENGANGSSTØNAD, FORELDREPENGER_V1
    }

    private static final Logger LOG = LoggerFactory.getLogger(Jaxb.class);
    private static final JAXBContext CTX_FPV1 = contextFra(
            no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.Endringssoeknad.class,
            no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger.class,
            no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.ObjectFactory.class,
            no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.ObjectFactory.class,
            no.nav.vedtak.felles.xml.soeknad.v1.Soeknad.class);
    private static final JAXBContext CTX_ES = contextFra(SoeknadsskjemaEngangsstoenad.class, Dokumentforsendelse.class);
    static final Schema FP_SCHEMA_V1 = fpSchema(SupportedVersion.V1);

    private Jaxb() {
    }

    private static final JAXBContext context(ValidationMode mode) {
        return mode.equals(ValidationMode.ENGANGSSTØNAD) ? CTX_ES : CTX_FPV1;
    }

    private static Schema fpSchema(SupportedVersion version) {
        try {
            return SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
                    .newSchema(sourcesFra(version,
                            "/foreldrepenger/foreldrepenger.xsd",
                            "/endringssoeknad-fpfordel.xsd",
                            "/soeknad-fpfordel.xsd"));
        } catch (SAXException e) {
            LOG.warn("Noe gikk galt med konfigurasjon av validering, bruker ikke-validerende marshaller");
            return null;
        }
    }

    public static <T> JAXBElement<T> unmarshalToElement(String xml, Class<T> clazz, ValidationMode mode) {
        return unmarshalToElement(context(mode), xml, clazz, mode);
    }

    public static String marshal(Object model, ValidationMode mode) {
        return marshal(context(mode), model, mode);
    }

    public static <T> T unmarshal(byte[] bytes, Class<T> clazz, ValidationMode mode) {
        return unmarshal(context(mode), bytes, clazz, mode);
    }

    public static <T> T unmarshal(String xml, Class<T> clazz, ValidationMode mode) {
        return unmarshal(context(mode), xml, clazz, mode);
    }

    private static JAXBContext contextFra(Class<?>... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Element marshalToElement(Object model, ValidationMode mode) {
        return marshalToElement(context(mode), model, mode);
    }

    private static Element marshalToElement(JAXBContext context, Object model, ValidationMode mode) {
        try {
            DOMResult res = new DOMResult();
            marshaller(context, mode).marshal(model, res);
            return ((Document) res.getNode()).getDocumentElement();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    private static String marshal(JAXBContext context, Object model, ValidationMode mode) {
        try {
            StringWriter sw = new StringWriter();
            marshaller(context, mode).marshal(model, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static Unmarshaller unmarshaller(ValidationMode mode) {
        return unmarshaller(context(mode), mode);
    }

    private static Unmarshaller unmarshaller(JAXBContext context, ValidationMode mode) {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new DefaultValidationEventHandler());
            switch (mode) {
            case ENGANGSSTØNAD:
                return unmarshaller;
            case FORELDREPENGER_V1:
                unmarshaller.setSchema(FP_SCHEMA_V1);
                return unmarshaller;
            }
            return unmarshaller;
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static <T> T unmarshal(JAXBContext context, byte[] bytes, Class<T> clazz, ValidationMode mode) {
        return unmarshal(context, new String(bytes), clazz, mode);
    }

    private static <T> T unmarshal(JAXBContext context, String xml, Class<T> clazz, ValidationMode mode) {
        try {
            return (T) unmarshaller(context, mode).unmarshal(new StringReader(unescapeHtml4(xml)));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static <T> JAXBElement<T> unmarshalToElement(JAXBContext context, String xml, Class<T> clazz,
            ValidationMode mode) {
        try {

            Unmarshaller unmarshaller = unmarshaller(context, mode);
            return (JAXBElement<T>) unmarshaller.unmarshal(new StringReader(unescapeHtml4(xml)));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static Marshaller marshaller(ValidationMode mode) {
        return marshaller(context(mode), mode);
    }

    private static Marshaller marshaller(JAXBContext context, ValidationMode mode) {
        Marshaller marshaller = createMarshaller(context);

        switch (mode) {
        case ENGANGSSTØNAD:
            return marshaller;
        case FORELDREPENGER_V1:
            if (FP_SCHEMA_V1 != null) {
                LOG.info("Kunne ha validerer XM, gjør det ikke");
                // marshaller.setSchema(Jaxb.FP_SCHEMA_V1);
            }
            else {
                LOG.info("Validerer ikke XML");
            }
            return marshaller;
        default:
            return marshaller;
        }
    }

    private static Marshaller createMarshaller(JAXBContext context) {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.setEventHandler(new DefaultValidationEventHandler());
            return marshaller;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Source[] sourcesFra(SupportedVersion version, String... schemas) {
        return Arrays.stream(schemas)
                .map(s -> version.name() + s)
                .map(Jaxb::sourceFra)
                .toArray(Source[]::new);

    }

    private static Source sourceFra(String re) {
        try {
            URL url = Jaxb.class.getClassLoader().getResource(re);
            return new StreamSource(inputStreamFra(new UrlResource(url)), url.toExternalForm());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static InputStream inputStreamFra(UrlResource res) {
        try {
            if (!res.exists()) {
                throw new IllegalStateException("Ressursen  " + res + " finnes ikke");
            }
            return res.getInputStream();
        } catch (IOException e) {
            throw new IllegalStateException("Ingen input stream for " + res, e);
        }
    }
}
