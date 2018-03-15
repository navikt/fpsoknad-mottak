package no.nav.foreldrepenger.mottak.domain.serialization;

import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.booleanValue;
import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.navn;
import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.textValue;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;

public class NorskForelderDeserializer extends StdDeserializer<NorskForelder> {

    private static final Logger LOG = LoggerFactory.getLogger(NorskForelderDeserializer.class);

    public NorskForelderDeserializer() {
        this(null);
    }

    public NorskForelderDeserializer(Class<NorskForelder> forelder) {
        super(forelder);
    }

    @Override
    public NorskForelder deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        LOG.info("Deserializing");
        JsonNode rootNode = p.getCodec().readTree(p);
        return new NorskForelder(booleanValue(rootNode, "lever", true), navn(rootNode),
                new Fødselsnummer(textValue(rootNode, "fnr")));
    }

}
