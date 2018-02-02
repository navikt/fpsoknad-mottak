package no.nav.foreldrepenger.mottak.domain;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class MedlemsskapSerializer extends StdSerializer<Medlemsskap> {

    public MedlemsskapSerializer() {
        this(null);
    }

    public MedlemsskapSerializer(Class<Medlemsskap> t) {
        super(t);
    }

    @Override
    public void serialize(Medlemsskap value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeBooleanField("iNorgeSiste12", value.getTidligereOppholdsInfo().isBoddINorge());
        jgen.writeStringField("arbeidSiste12", value.getTidligereOppholdsInfo().getArbeidsInfo().name());

        if (!value.getTidligereOppholdsInfo().getUtenlandsOpphold().isEmpty()) {
            jgen.writeObjectField("utenlandsopphold", value.getTidligereOppholdsInfo().getUtenlandsOpphold());
        }

        jgen.writeBooleanField("fødselINorge", value.getFremtidigOppholdsInfo().isFødseINorge());
        jgen.writeBooleanField("iNorgeNeste12", value.getFremtidigOppholdsInfo().isNorgeNeste12());
        jgen.writeEndObject();
    }
}
