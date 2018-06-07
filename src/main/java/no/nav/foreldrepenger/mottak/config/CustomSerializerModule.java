package no.nav.foreldrepenger.mottak.config;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;

import no.nav.foreldrepenger.mottak.domain.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.serialization.MedlemsskapDeserializer;
import no.nav.foreldrepenger.mottak.domain.serialization.MedlemsskapSerializer;
import no.nav.foreldrepenger.mottak.domain.serialization.NorskForelderDeserializer;
import no.nav.foreldrepenger.mottak.domain.serialization.NorskForelderSerializer;
import no.nav.foreldrepenger.mottak.domain.serialization.UtenlandskForelderDeserializer;
import no.nav.foreldrepenger.mottak.domain.serialization.UtenlandskForelderSerializer;

public class CustomSerializerModule extends SimpleModule {

    public CustomSerializerModule() {
        super(PackageVersion.VERSION);
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
