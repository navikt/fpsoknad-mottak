package no.nav.foreldrepenger.mottak.domain.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import no.nav.foreldrepenger.mottak.domain.felles.NorskForelder;

public class NorskForelderSerializer extends StdSerializer<NorskForelder> {

    public NorskForelderSerializer() {
        this(null);
    }

    public NorskForelderSerializer(Class<NorskForelder> t) {
        super(t);
    }

    @Override
    public void serialize(NorskForelder forelder, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStringField("fnr", forelder.getFnr().getFnr());
        gen.writeStringField("navn", forelder.getNavn());
    }

    @Override
    public void serializeWithType(NorskForelder value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSerializer) throws IOException {
        WritableTypeId typeId = typeSerializer.typeId(value, JsonToken.START_OBJECT);
        typeSerializer.writeTypePrefix(gen, typeId);
        serialize(value, gen, provider);
        typeSerializer.writeTypeSuffix(gen, typeId);

    }

}
