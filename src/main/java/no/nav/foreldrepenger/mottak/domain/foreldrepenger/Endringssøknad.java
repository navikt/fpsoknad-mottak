package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

public class Endringssøknad extends Søknad {

    @NotNull
    private final String saksnr;

    public Endringssøknad(Søker søker, Fordeling fordeling, AnnenForelder annenForelder,
            Fødsel fødsel, Rettigheter rettigheter, String saksnr,
            Vedlegg... vedlegg) {
        this(now(), søker, fordeling, annenForelder, fødsel, rettigheter, saksnr, vedlegg);
    }

    public Endringssøknad(LocalDateTime mottattDato, Søker søker, Fordeling fordeling, String saksnr,
            Vedlegg... vedlegg) {
        this(mottattDato, søker, fordeling, null, null, null, saksnr, vedlegg);
    }

    public Endringssøknad(LocalDateTime mottattDato, Søker søker, Fordeling fordeling, AnnenForelder annenForelder,
            Fødsel fødsel, Rettigheter rettigheter, String saksnr,
            Vedlegg... vedlegg) {
        this(mottattDato, søker, annenForelder, fordeling, fødsel, rettigheter, saksnr, asList(vedlegg));
    }

    @JsonCreator
    public Endringssøknad(@JsonProperty("mottattdato") LocalDateTime mottattDato, @JsonProperty("søker") Søker søker,
            @JsonProperty("annenForelder") AnnenForelder annenForelder,
            @JsonProperty("fordeling") Fordeling fordeling,
            @JsonProperty("fødsel") Fødsel fødsel,
            @JsonProperty("rettigheter") Rettigheter rettigheter,
            @JsonProperty("saksnr") String saksnr,
            @JsonProperty("vedlegg") List<Vedlegg> vedlegg) {
        super(mottattDato, søker, new Foreldrepenger(annenForelder, fødsel, rettigheter, null, null, fordeling, null),
                vedlegg);
        this.saksnr = saksnr;
    }

    public String getSaksnr() {
        return saksnr;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [saksnr=" + saksnr + ", getYtelse()=" + getYtelse() + "]";
    }

}
