package no.nav.foreldrepenger.mottak.domain.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;

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

        jgen.writeObjectField("arbeidSiste12", value.getTidligereOppholdsInfo().getArbeidSiste12());
        if (!value.getTidligereOppholdsInfo().getUtenlandsOpphold().isEmpty()) {
            jgen.writeObjectField("utenlandsopphold", value.getTidligereOppholdsInfo().getUtenlandsOpphold());
        }

        if (!value.getFramtidigOppholdsInfo().getUtenlandsOpphold().isEmpty()) {
            jgen.writeObjectField("framtidigUtenlandsopphold", value.getFramtidigOppholdsInfo().getUtenlandsOpphold());
        }
        jgen.writeEndObject();
    }
}
