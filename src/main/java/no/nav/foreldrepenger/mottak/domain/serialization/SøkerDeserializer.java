package no.nav.foreldrepenger.mottak.domain.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Fodselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
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
        return new Søker(new Fodselsnummer(textValue(rootNode, "fnr")), new AktorId(textValue(rootNode, "aktør")),
                BrukerRolle.valueOf(textValue(rootNode, "søknadsRolle")), navn(rootNode));
    }

    private static Navn navn(JsonNode rootNode) {
        return new Navn(textValue(rootNode, "fornavn"), textValue(rootNode, "mellomnavn"),
                textValue(rootNode, "etternavn"));
    }

    private static String textValue(JsonNode rootNode, String fieldName) {
        return TextNode.class.cast(rootNode.get(fieldName)).textValue();
    }
}
