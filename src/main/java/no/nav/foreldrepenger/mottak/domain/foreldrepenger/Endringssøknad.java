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

    public Endringssøknad(LocalDateTime mottattDato, Søker søker, Fordeling fordeling, String saksnr,
            Vedlegg... vedlegg) {
        this(mottattDato, søker, fordeling, saksnr, asList(vedlegg));
    }

    @JsonCreator
    public Endringssøknad(@JsonProperty("mottattDato") LocalDateTime mottattDato, @JsonProperty("søker") Søker søker,
            @JsonProperty("fordeling") Fordeling fordeling,
            @JsonProperty("saksnr") String saksnr,
            @JsonProperty("vedlegg") List<Vedlegg> vedlegg) {
        super(mottattDato, søker, new Foreldrepenger(null, null, null, null, null, fordeling, null), vedlegg);
        this.saksnr = saksnr;
    }

    public String getSaksnr() {
        return saksnr;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [saksnr=" + saksnr + ", fordeling="
                + Foreldrepenger.class.cast(getYtelse()).getFordeling() + "]";
    }

}
