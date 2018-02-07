package no.nav.foreldrepenger.mottak.domain;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public class BrukerDeserializer extends StdDeserializer<Bruker> {

    public BrukerDeserializer() {
        this(null);
    }

    public BrukerDeserializer(Class<Bruker> t) {
        super(t);
    }

    @Override
    public Bruker deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);
        JsonNode type = rootNode.get("type");
        return new AktorId(rootNode.get("value").asText());
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
            throws IOException {
        // TODO Auto-generated method stub
        return super.deserializeWithType(p, ctxt, typeDeserializer);
    }

}
