package no.nav.foreldrepenger.mottak.util;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static javax.xml.bind.Marshaller.JAXB_FRAGMENT;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class Jaxb {

    private static final SchemaFactory SF = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);

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
            return context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T unmarshal(byte[] bytes, JAXBContext context, Class<T> clazz) {
        return unmarshal(new String(bytes), context, clazz);
    }

    public static <T> T unmarshal(String xml, JAXBContext context, Class<T> clazz) {
        try {
            return (T) unmarshaller(context).unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> JAXBElement<T> unmarshalToElement(String xml, JAXBContext context, Class<T> clazz) {
        try {
            StreamSource[] sources = new StreamSource[5];
            sources[0] = new StreamSource(new ClassPathResource("kodeverk/kodeverk.xsd").getInputStream());
            sources[1] = new StreamSource(new ClassPathResource("felles/felles.xsd").getInputStream());
            sources[2] = new StreamSource(new ClassPathResource("foreldrepenger/foreldrepenger.xsd").getInputStream());
            sources[3] = new StreamSource(new ClassPathResource("uttak/uttak.xsd").getInputStream());
            sources[4] = new StreamSource(new ClassPathResource("soeknad.xsd").getInputStream());

            Unmarshaller unmarshaller = unmarshaller(context);
            // unmarshaller.setSchema((SF.newSchema(sources)));
            return (JAXBElement<T>) unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    private static Marshaller marshaller(JAXBContext context, boolean isFragment) {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(JAXB_FRAGMENT, isFragment);
            return marshaller;
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
