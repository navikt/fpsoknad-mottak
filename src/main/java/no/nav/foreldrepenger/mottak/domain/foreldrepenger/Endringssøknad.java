package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Arrays.asList;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

public class Endringssøknad extends Søknad {

    private final String saksnr;

    public Endringssøknad(LocalDateTime mottattDato, Søker søker, Fordeling fordeling, AnnenForelder annenForelder,
            RelasjonTilBarnMedVedlegg relasjonTilBarn, Rettigheter rettigheter, String saksnr,
            Vedlegg... vedlegg) {
        this(mottattDato, søker, annenForelder, fordeling, relasjonTilBarn, rettigheter, saksnr, asList(vedlegg));
    }

    @JsonCreator
    public Endringssøknad(@JsonProperty("mottattDato") LocalDateTime mottattDato, @JsonProperty("søker") Søker søker,
            @JsonProperty("annenForelder") AnnenForelder annenForelder,
            @JsonProperty("fordeling") Fordeling fordeling,
            @JsonProperty("relasjonTilBarn") RelasjonTilBarnMedVedlegg relasjonTilBarn,
            @JsonProperty("rettigheter") Rettigheter rettigheter,
            @JsonProperty("saksnr") String saksnr,
            @JsonProperty("vedlegg") List<Vedlegg> vedlegg) {
        super(mottattDato, søker,
                new Foreldrepenger(annenForelder, relasjonTilBarn, rettigheter, null, null, fordeling, null),
                vedlegg);
        this.saksnr = saksnr;
    }

    public String getSaksnr() {
        return saksnr;
    }

    @Override
    public String toString() {
        Foreldrepenger ytelse = Foreldrepenger.class.cast(getYtelse());
        return getClass().getSimpleName() + " [saksnr=" + saksnr + ", fordeling=" + ytelse.getFordeling()
                + ", relasjonTilBarn=" + ytelse.getRelasjonTilBarn() + "]";
    }
}
