package no.nav.foreldrepenger.mottak.domain.serialization;

import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.doubleValue;
import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.fromNumber;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;

public class ProsentAndelDeserializer extends StdDeserializer<ProsentAndel> {

    public ProsentAndelDeserializer() {
        this(null);
    }

    public ProsentAndelDeserializer(Class<ProsentAndel> t) {
        super(t);
    }

    @Override
    public ProsentAndel deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        JsonNode rootNode = p.getCodec().readTree(p);
        if (rootNode instanceof DoubleNode d) {
            return new ProsentAndel(doubleValue(d));
        }
        if (rootNode instanceof ObjectNode o) {
            return new ProsentAndel(fromNumber(o));
        }
        throw new UnexpectedInputException("Ukjent node type %s", rootNode.getClass().getSimpleName());
    }

}
