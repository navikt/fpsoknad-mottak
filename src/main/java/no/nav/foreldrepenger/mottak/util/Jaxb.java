package no.nav.foreldrepenger.mottak.util;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;

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
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;
import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

public final class Jaxb {

    public enum ValidationMode {
        INGEN, ENGANGSSTØNAD, FORELDREPENGER

    }

    private static final Logger LOG = LoggerFactory.getLogger(Jaxb.class);
    private static final JAXBContext CONTEXT = context(Soeknad.class, Endringssoeknad.class, Foreldrepenger.class,
            SoeknadsskjemaEngangsstoenad.class, Dokumentforsendelse.class);

    // private static final Schema FP_SCHEMA = fpSchema();

    private Jaxb() {

    }

    private static Schema fpSchema() {
        try {
            return SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
                    .newSchema(sourcesFra(
                            "/soeknad-fpfordel.xsd",
                            "/endringssoeknad-fpfordel.xsd",
                            "/felles/felles.xsd",
                            "/foreldrepenger/foreldrepenger.xsd",
                            "/uttak/uttak.xsd",
                            "/kodeverk/kodeverk.xsd"));
        } catch (SAXException e) {
            return null; // for now
            // throw new IllegalStateException(e);
        }
    }

    public static <T> JAXBElement<T> unmarshalToElement(String xml, Class<T> clazz) {
        return unmarshalToElement(CONTEXT, xml, clazz);
    }

    public static String marshal(Object model, ValidationMode mode) {
        return marshal(CONTEXT, model, mode);
    }

    public static <T> T unmarshal(byte[] bytes, Class<T> clazz) {
        return unmarshal(CONTEXT, bytes, clazz);
    }

    public static <T> T unmarshal(String xml, Class<T> clazz) {
        return unmarshal(CONTEXT, xml, clazz);
    }

    private static JAXBContext context(Class<?>... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Element marshalToElement(Object model, ValidationMode mode) {
        return marshalToElement(CONTEXT, model, mode);
    }

    private static Element marshalToElement(JAXBContext context, Object model, ValidationMode mode) {
        try {
            DOMResult res = new DOMResult();
            marshaller(context, mode).marshal(model, res);
            return ((Document) res.getNode()).getDocumentElement();
        } catch (JAXBException e) {
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

    private static Unmarshaller unmarshaller(JAXBContext context) {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new DefaultValidationEventHandler());
            return unmarshaller;
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static <T> T unmarshal(JAXBContext context, byte[] bytes, Class<T> clazz) {
        return unmarshal(context, new String(bytes), clazz);
    }

    private static <T> T unmarshal(JAXBContext context, String xml, Class<T> clazz) {
        try {
            return (T) unmarshaller(context).unmarshal(new StringReader(unescapeHtml4(xml)));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static <T> JAXBElement<T> unmarshalToElement(JAXBContext context, String xml, Class<T> clazz) {
        try {

            Unmarshaller unmarshaller = unmarshaller(context);
            return (JAXBElement<T>) unmarshaller.unmarshal(new StringReader(unescapeHtml4(xml)));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Marshaller marshaller(JAXBContext context, ValidationMode mode) {
        Marshaller marshaller = null;
        try {
            marshaller = context.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.setEventHandler(new DefaultValidationEventHandler());
            switch (mode) {
            case INGEN:
                return marshaller;
            case ENGANGSSTØNAD:
                return marshaller;
            case FORELDREPENGER:
                Schema schema = fpSchema();
                if (schema != null) {
                    LOG.info("Validating XML, using schena {}", schema);
                }
                else {
                    LOG.info("NOT Validating XML, we do not have all the schema definitions");
                }
                // marshaller.setSchema(schema);
                return marshaller;
            default:
                return marshaller;
            }
        } catch (Exception e) {
            return Optional.ofNullable(marshaller).orElseThrow(() -> new IllegalStateException(e));
        }
    }

    private static Source[] sourcesFra(String... schemas) {
        return Arrays.stream(schemas)
                .map(ClassPathResource::new)
                .filter(ClassPathResource::exists)
                .map(Jaxb::inputStream)
                .map(StreamSource::new)
                .toArray(Source[]::new);

    }

    private static InputStream inputStream(ClassPathResource res) {
        try {
            InputStream is = res.getInputStream();
            LOG.trace("La til schema fra {}", res.getFilename());
            return is;
        } catch (IOException e) {
            throw new IllegalStateException("Ingen input stream for " + res.getFilename(), e);
        }

    }
}
