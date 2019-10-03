package no.nav.foreldrepenger.mottak.util.jaxb;

import static java.lang.String.format;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;

public abstract class AbstractJAXBUtil {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractJAXBUtil.class);
    private final JAXBContext context;
    private final Schema schema;
    private final boolean validateMarshalling;
    private final boolean validateUnarshalling;

    public AbstractJAXBUtil(JAXBContext context, boolean validateMarhsalling,
            boolean validateUnmarshalling, String... xsds) {
        this.context = context;
        this.schema = schemaFra(xsds);
        this.validateMarshalling = validateMarhsalling;
        this.validateUnarshalling = validateUnmarshalling;
    }

    protected static JAXBContext contextFra(Class<?>... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            throw new UnexpectedInputException(
                    format("Feil ved konfigurasjon av kontekst fra %s", Arrays.toString(classes)), e);
        }
    }

    public Element marshalToElement(Object model) {
        try {
            DOMResult res = new DOMResult();
            marshaller().marshal(model, res);
            return ((Document) res.getNode()).getDocumentElement();
        } catch (JAXBException e) {
            throw new UnexpectedInputException(format("Feil ved marshalling av model %s", model.getClass()), e);
        }
    }

    public String marshal(Object model) {
        try {
            StringWriter sw = new StringWriter();
            marshaller().marshal(model, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new UnexpectedInputException(format("Feil ved marshalling av model %s", model.getClass()), e);
        }
    }

    public <T> T unmarshal(byte[] bytes, Class<T> clazz) {
        return unmarshal(new String(bytes), clazz);
    }

    public <T> T unmarshal(String xml, Class<T> clazz) {
        try {
            return (T) unmarshaller().unmarshal(new StringReader(unescapeHtml4(xml)));
        } catch (JAXBException e) {
            throw new UnexpectedInputException(format("Feil ved unmarshalling av %s til  %s", xml, clazz.getName()), e);
        }
    }

    public <T> JAXBElement<T> unmarshalToElement(String xml, Class<T> clazz) {
        try {
            return (JAXBElement<T>) unmarshaller().unmarshal(new StringReader(unescapeHtml4(xml)));
        } catch (JAXBException e) {
            throw new UnexpectedInputException(format("Feil ved unmarshalling av %s til  %s", xml, clazz.getName()), e);
        }
    }

    private static Schema schemaFra(String... xsds) {
        try {
            return SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(sourcesFra(xsds));
        } catch (SAXException e) {
            LOG.warn(
                    "Noe gikk galt med konfigurasjon av skjema fra {}, bruker ikke-validerende marshaller",
                    Arrays.toString(xsds), e);
            return null;
        }
    }

    public Unmarshaller unmarshaller() {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new DefaultValidationEventHandler());
            if (schema != null && validateUnarshalling) {
                unmarshaller.setSchema(schema);
            }
            return unmarshaller;
        } catch (JAXBException e) {
            throw new UnexpectedInputException(
                    format("Feil ved konstruksjon av unmarshaller fra kontekst %s og skjema  %s", context, schema), e);
        }
    }

    private Marshaller marshaller() {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.setEventHandler(new DefaultValidationEventHandler());
            if (schema != null && validateMarshalling) {
                marshaller.setSchema(schema);
            }
            return marshaller;
        } catch (JAXBException e) {
            throw new UnexpectedInputException(
                    format("Feil ved konstruksjon av marshaller fra kontekst %s og skjema  %s", context, schema), e);
        }
    }

    private static Source[] sourcesFra(String... schemas) {
        return Arrays.stream(schemas)
                .map(AbstractJAXBUtil::sourceFra)
                .toArray(Source[]::new);
    }

    private static Source sourceFra(String re) {
        try {
            return sourceFra(new ClassPathResource(re));
        } catch (Exception e) {
            throw new UnexpectedInputException(format("Noe gikk galt ved lesing av ressurs %s fra classpath", re), e);
        }
    }

    private static Source sourceFra(ClassPathResource cpr) throws IOException {
        return new StreamSource(inputStreamFra(cpr), cpr.getURI().toURL().toExternalForm());
    }

    private static InputStream inputStreamFra(Resource res) {
        try {
            if (!res.exists()) {
                throw new UnexpectedInputException(format("Ressursen  %s finnes ikke", res));
            }
            return res.getInputStream();
        } catch (IOException e) {
            throw new UnexpectedInputException(format("Ingen input stream for %s", res));
        }
    }

    public static Double tilDoubleFraBigDecimal(JAXBElement<BigDecimal> value) {
        return Optional.ofNullable(value)
                .map(JAXBElement::getValue)
                .map(BigDecimal::doubleValue)
                .orElse(null);
    }

    public static Double tilDoubleFraBigInteger(JAXBElement<BigInteger> value) {
        return Optional.ofNullable(value)
                .map(JAXBElement::getValue)
                .map(BigInteger::doubleValue)
                .orElse(null);
    }

    public static boolean tilBoolean(JAXBElement<Boolean> value) {
        return Optional.ofNullable(value)
                .map(JAXBElement::getValue)
                .orElse(false);
    }

    public static String tilTekst(JAXBElement<String> tekst) {
        return Optional.ofNullable(tekst)
                .map(JAXBElement::getValue)
                .orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [context=" + context + ", schema=" + schema + "]";
    }
}
