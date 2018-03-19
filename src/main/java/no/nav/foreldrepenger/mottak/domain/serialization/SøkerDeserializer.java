package no.nav.foreldrepenger.mottak.domain.serialization;

import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.navn;
import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.textValue;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søker;

public class SøkerDeserializer extends StdDeserializer<Søker> {

    public SøkerDeserializer() {
        this(null);
    }

    public SøkerDeserializer(Class<Søker> søker) {
        super(søker);
    }

    @Override
    public Søker deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);
        return new Søker(new Fødselsnummer(textValue(rootNode, "fnr")), new AktorId(textValue(rootNode, "aktør")),
                BrukerRolle.valueOf(textValue(rootNode, "søknadsRolle")), navn(rootNode));
    }

}
