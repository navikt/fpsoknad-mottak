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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;
import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

public final class Jaxb {

    private static final JAXBContext CONTEXT = context(Soeknad.class, Endringssoeknad.class, Foreldrepenger.class,
            SoeknadsskjemaEngangsstoenad.class, Dokumentforsendelse.class);

    private Jaxb() {

    }

    public static Element marshalToElement(Object model) {
        return marshalToElement(CONTEXT, model);
    }

    public static <T> JAXBElement<T> unmarshalToElement(String xml, Class<T> clazz) {
        return unmarshalToElement(CONTEXT, xml, clazz);
    }

    public static String marshal(Object model) {
        return marshal(CONTEXT, model);
    }

    public static <T> T unmarshal(byte[] bytes, Class<T> clazz) {
        return unmarshal(CONTEXT, bytes, clazz);
    }

    private static JAXBContext context(Class<?>... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String marshal(JAXBContext context, Object model) {
        return marshal(context, model, true);
    }

    private static Element marshalToElement(JAXBContext context, Object model) {
        try {
            DOMResult res = new DOMResult();
            marshaller(context, true).marshal(model, res);
            return ((Document) res.getNode()).getDocumentElement();
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String marshal(JAXBContext context, Object model, boolean isFragment) {
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

    private static <T> T unmarshal(JAXBContext context, byte[] bytes, Class<T> clazz) {
        return unmarshal(context, new String(bytes), clazz);
    }

    public static <T> T unmarshal(String xml, Class<T> clazz) {
        return unmarshal(CONTEXT, xml, clazz);
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
