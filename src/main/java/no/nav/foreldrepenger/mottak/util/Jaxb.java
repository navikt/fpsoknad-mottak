package no.nav.foreldrepenger.mottak.util;

import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public final class Jaxb {

    private Jaxb() {

    }

    public static JAXBContext context(Class<?> clazz) {
        try {
            return JAXBContext.newInstance(clazz);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String marshall(JAXBContext context, Object model) {
        try {
            StringWriter sw = new StringWriter();
            marshaller(context).marshal(model, sw);
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

    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(String xml, JAXBContext context, Class<T> clazz) {
        try {
            return (T) unmarshaller(context).unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Marshaller marshaller(JAXBContext context) {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            return marshaller;
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
