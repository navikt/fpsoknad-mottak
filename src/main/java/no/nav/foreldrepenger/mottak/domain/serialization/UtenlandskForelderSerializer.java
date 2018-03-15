package no.nav.foreldrepenger.mottak.domain.serialization;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;

public class UtenlandskForelderSerializer extends StdSerializer<UtenlandskForelder> {

    private static final Logger LOG = LoggerFactory.getLogger(UtenlandskForelderSerializer.class);

    public UtenlandskForelderSerializer() {
        this(null);
    }

    public UtenlandskForelderSerializer(Class<UtenlandskForelder> t) {
        super(t);
    }

    @Override
    public void serialize(UtenlandskForelder forelder, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        LOG.info("Serializing");
        jgen.writeBooleanField("lever", forelder.isLever());
        jgen.writeStringField("id", forelder.getId());
        jgen.writeStringField("land", forelder.getLand().name());
        JacksonUtils.writeNavn(forelder.getNavn(), jgen);
    }

    @Override
    public void serializeWithType(UtenlandskForelder value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer) throws IOException {
        WritableTypeId typeId = typeSer.typeId(value, JsonToken.START_OBJECT);
        typeSer.writeTypePrefix(gen, typeId);
        serialize(value, gen, provider);
        typeSer.writeTypeSuffix(gen, typeId);

    }
}
