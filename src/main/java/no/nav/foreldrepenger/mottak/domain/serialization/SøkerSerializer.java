package no.nav.foreldrepenger.mottak.domain.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import no.nav.foreldrepenger.mottak.domain.Søker;

public class SøkerSerializer extends StdSerializer<Søker> {

    public SøkerSerializer() {
        this(null);
    }

    public SøkerSerializer(Class<Søker> t) {
        super(t);
    }

    @Override
    public void serialize(Søker søker, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("aktør", søker.getAktør().getId());
        jgen.writeStringField("fnr", søker.getFnr().getId());
        jgen.writeStringField("søknadsRolle", søker.getSøknadsRolle().name());
        jgen.writeStringField("fornavn", søker.getNavn().getFornavn());
        jgen.writeStringField("mellomnavn", søker.getNavn().getMellomnavn());
        jgen.writeStringField("etternavn", søker.getNavn().getEtternavn());
        jgen.writeEndObject();
    }
}
