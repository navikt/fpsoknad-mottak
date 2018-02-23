package no.nav.foreldrepenger.mottak.domain.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import no.nav.foreldrepenger.mottak.domain.Medlemsskap;

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
        jgen.writeBooleanField("norgeSiste12", value.getTidligereOppholdsInfo().isBoddINorge());
        jgen.writeStringField("arbeidSiste12", value.getTidligereOppholdsInfo().getArbeidsInfo().name());

        if (!value.getTidligereOppholdsInfo().getUtenlandsOpphold().isEmpty()) {
            jgen.writeObjectField("utenlandsopphold", value.getTidligereOppholdsInfo().getUtenlandsOpphold());
        }

        jgen.writeBooleanField("fødselNorge", value.getFramtidigOppholdsInfo().isFødselNorge());
        jgen.writeBooleanField("norgeNeste12", value.getFramtidigOppholdsInfo().isNorgeNeste12());
        if (!value.getFramtidigOppholdsInfo().getUtenlandsOpphold().isEmpty()) {
            jgen.writeObjectField("framtidigUtenlandsopphold", value.getFramtidigOppholdsInfo().getUtenlandsOpphold());
        }
        jgen.writeEndObject();
    }
}
