package no.nav.foreldrepenger.mottak.domain;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class BrukerSerializer extends StdSerializer<Bruker> {

    public BrukerSerializer() {
        this(null);
    }

    public BrukerSerializer(Class<Bruker> t) {
        super(t);
    }

    @Override
    public void serialize(Bruker value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStringField("value", value.getValue());
    }

    @Override
    public void serializeWithType(Bruker value, JsonGenerator gen,
            SerializerProvider provider, TypeSerializer typeSer)
            throws IOException, JsonProcessingException {
        WritableTypeId typeId = typeSer.typeId(value, JsonToken.START_OBJECT);
        typeSer.writeTypePrefix(gen, typeId);
        serialize(value, gen, provider);
        typeSer.writeTypeSuffix(gen, typeId);
    }
}
