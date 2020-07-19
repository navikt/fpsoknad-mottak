package no.nav.foreldrepenger.mottak.domain.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UtenlandskForelder;

public class UtenlandskForelderSerializer extends StdSerializer<UtenlandskForelder> {

    public UtenlandskForelderSerializer() {
        this(null);
    }

    public UtenlandskForelderSerializer(Class<UtenlandskForelder> t) {
        super(t);
    }

    @Override
    public void serialize(UtenlandskForelder forelder, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeStringField("id", forelder.getId());
        jgen.writeStringField("land", forelder.getLand().name());
        jgen.writeStringField("navn", forelder.getNavn());
    }

    @Override
    public void serializeWithType(UtenlandskForelder value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSerializer) throws IOException {
        var typeId = typeSerializer.typeId(value, JsonToken.START_OBJECT);
        typeSerializer.writeTypePrefix(gen, typeId);
        serialize(value, gen, provider);
        typeSerializer.writeTypeSuffix(gen, typeId);

    }
}
