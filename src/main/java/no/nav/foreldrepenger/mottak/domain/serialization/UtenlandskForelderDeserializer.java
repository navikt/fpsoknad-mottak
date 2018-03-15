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
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;

public class UtenlandskForelderDeserializer extends StdDeserializer<UtenlandskForelder> {

    private static final Logger LOG = LoggerFactory.getLogger(UtenlandskForelderDeserializer.class);

    public UtenlandskForelderDeserializer() {
        this(null);
    }

    public UtenlandskForelderDeserializer(Class<UtenlandskForelder> forelder) {
        super(forelder);
    }

    @Override
    public UtenlandskForelder deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        LOG.info("Deserializing");
        JsonNode rootNode = p.getCodec().readTree(p);
        return new UtenlandskForelder(booleanValue(rootNode, "lever", true), navn(rootNode), textValue(rootNode, "id"),
                CountryCode.valueOf(textValue(rootNode, "land")));
    }

}
