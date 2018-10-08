package no.nav.foreldrepenger.mottak.util;

import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static javax.xml.bind.Marshaller.JAXB_FRAGMENT;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.transform.dom.DOMResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

public final class Jaxb {

    private static final Logger LOG = LoggerFactory.getLogger(Jaxb.class);

    public static final JAXBContext CONTEXT = context(Soeknad.class, Endringssoeknad.class, Foreldrepenger.class);

    private Jaxb() {

    }

    public static JAXBContext context(Class<?>... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Element marshalToElement(JAXBContext context, Object model) {
        try {
            DOMResult res = new DOMResult();
            marshaller(context, true).marshal(model, res);
            return ((Document) res.getNode()).getDocumentElement();
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String marshal(JAXBContext context, Object model) {
        return marshal(context, model, true);
    }

    public static String marshal(JAXBContext context, Object model, boolean isFragment) {
        try {
            StringWriter sw = new StringWriter();
            marshaller(context, isFragment).marshal(model, sw);
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

    public static <T> T unmarshal(byte[] bytes, JAXBContext context, Class<T> clazz) {
        return unmarshal(new String(bytes), context, clazz);
    }

    public static <T> T unmarshal(String xml, JAXBContext context, Class<T> clazz) {
        try {
            return (T) unmarshaller(context).unmarshal(new StringReader(unescapeHtml4(xml)));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> JAXBElement<T> unmarshalToElement(String xml, Class<T> clazz) {
        return unmarshalToElement(xml, CONTEXT, clazz);
    }

    private static <T> JAXBElement<T> unmarshalToElement(String xml, JAXBContext context, Class<T> clazz) {
        try {

            Unmarshaller unmarshaller = unmarshaller(context);
            return (JAXBElement<T>) unmarshaller.unmarshal(new StringReader(unescapeHtml4(xml)));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Marshaller marshaller(JAXBContext context, boolean isFragment) {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(JAXB_FRAGMENT, isFragment);
            marshaller.setEventHandler(new DefaultValidationEventHandler());
            return marshaller;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
