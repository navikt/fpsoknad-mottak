package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.Periode;

@Data
@Periode
@JsonPropertyOrder({ "fom", "tom" })
public class LukketPeriode {

    private static final Logger LOG = LoggerFactory.getLogger(LukketPeriode.class);

    @NotNull
    private final LocalDate fom;
    @NotNull
    private final LocalDate tom;

    @JsonCreator
    public LukketPeriode(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }

    public boolean overlapper(LukketPeriode annenPeriode) {
        LOG.info("Sammeligner {} med {}", this, annenPeriode);
        boolean overlapper = inneholderDato(annenPeriode.getFom()) || inneholderDato(annenPeriode.getTom());
        LOG.info("Periodene overlapper {}", overlapper ? "" : "IKKE");
        return overlapper;
    }

    private boolean inneholderDato(LocalDate annenDato) {
        return annenDato.isAfter(this.getFom()) && annenDato.isBefore(this.getTom());
    }
}
