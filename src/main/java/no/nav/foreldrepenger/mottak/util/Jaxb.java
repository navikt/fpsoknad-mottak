package no.nav.foreldrepenger.mottak.util;

import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.transform.dom.DOMResult;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;
import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

public final class Jaxb {

    private static final Logger LOG = LoggerFactory.getLogger(Jaxb.class);
    private static final JAXBContext CONTEXT = context(Soeknad.class, Endringssoeknad.class, Foreldrepenger.class,
            SoeknadsskjemaEngangsstoenad.class, Dokumentforsendelse.class);

    private static final SchemaFactory SF = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    private Jaxb() {

    }

    public static Element marshalToElement(Object model) {
        return marshalToElement(CONTEXT, model, true);
    }

    public static <T> JAXBElement<T> unmarshalToElement(String xml, Class<T> clazz) {
        return unmarshalToElement(CONTEXT, xml, clazz);
    }

    public static String marshal(Object model, boolean validate) {
        return marshal(CONTEXT, model, validate);
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

    private static Element marshalToElement(JAXBContext context, Object model, boolean validate) {
        try {
            DOMResult res = new DOMResult();
            marshaller(context, validate).marshal(model, res);
            return ((Document) res.getNode()).getDocumentElement();
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String marshal(JAXBContext context, Object model, boolean validate) {
        try {
            StringWriter sw = new StringWriter();
            marshaller(context, validate).marshal(model, sw);
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

    private static Marshaller marshaller(JAXBContext context, boolean validate) {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.setEventHandler(new DefaultValidationEventHandler());
            if (validate) {
                LOG.info("Validating XML");
                ClassPathResource ss = new ClassPathResource("kodverk/kodeverk.xsd");
                LOG.trace("TESTING TESTING " + ss.exists());
                // Schema schema = SF.newSchema(new File("Employee.xsd"));
                // marshaller.setSchema(schema);
            }
            else {
                LOG.info("NOT validating XML");
            }
            return marshaller;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
