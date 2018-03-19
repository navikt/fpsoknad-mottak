package no.nav.foreldrepenger.mottak.domain.serialization;

import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.booleanValue;
import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.navn;
import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.textValue;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;

public class NorskForelderDeserializer extends StdDeserializer<NorskForelder> {

    public NorskForelderDeserializer() {
        this(null);
    }

    public NorskForelderDeserializer(Class<NorskForelder> forelder) {
        super(forelder);
    }

    @Override
    public NorskForelder deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);
        return new NorskForelder(booleanValue(rootNode, "lever", true), navn(rootNode),
                new Fødselsnummer(textValue(rootNode, "fnr")));
    }

}
