package no.nav.foreldrepenger.mottak.domain.serialization;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import no.nav.foreldrepenger.mottak.domain.Søker;

public class SøkerSerializer extends StdSerializer<Søker> {

    private static final Logger LOG = LoggerFactory.getLogger(SøkerSerializer.class);

    public SøkerSerializer() {
        this(null);
    }

    public SøkerSerializer(Class<Søker> t) {
        super(t);
    }

    @Override
    public void serialize(Søker søker, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        LOG.info("Serializing");
        jgen.writeStartObject();
        jgen.writeStringField("aktør", søker.getAktør().getId());
        jgen.writeStringField("fnr", søker.getFnr().getFnr());
        jgen.writeStringField("søknadsRolle", søker.getSøknadsRolle().name());
        JacksonUtils.writeNavn(søker.getNavn(), jgen);
        jgen.writeEndObject();
    }
}
