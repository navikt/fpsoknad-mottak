package no.nav.foreldrepenger.mottak.config;

import static com.fasterxml.jackson.core.json.PackageVersion.VERSION;

import com.fasterxml.jackson.databind.module.SimpleModule;

import no.nav.foreldrepenger.mottak.domain.felles.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.serialization.MedlemsskapDeserializer;
import no.nav.foreldrepenger.mottak.domain.serialization.MedlemsskapSerializer;
import no.nav.foreldrepenger.mottak.domain.serialization.NorskForelderDeserializer;
import no.nav.foreldrepenger.mottak.domain.serialization.NorskForelderSerializer;
import no.nav.foreldrepenger.mottak.domain.serialization.UtenlandskForelderDeserializer;
import no.nav.foreldrepenger.mottak.domain.serialization.UtenlandskForelderSerializer;

public class CustomSerializerModule extends SimpleModule {

    public CustomSerializerModule() {
        super(VERSION);
        addSerializers();
        addDeserializers();
    }

    private void addDeserializers() {
        addDeserializer(Medlemsskap.class, new MedlemsskapDeserializer());
        addDeserializer(UtenlandskForelder.class, new UtenlandskForelderDeserializer());
        addDeserializer(NorskForelder.class, new NorskForelderDeserializer());
    }

    private void addSerializers() {
        addSerializer(UtenlandskForelder.class, new UtenlandskForelderSerializer());
        addSerializer(Medlemsskap.class, new MedlemsskapSerializer());
        addSerializer(NorskForelder.class, new NorskForelderSerializer());
    }
}
