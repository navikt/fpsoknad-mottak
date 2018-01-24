package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;
import java.time.LocalDate;

import lombok.Data;

@Data
public class TerminInfo {
    private final LocalDate terminDato;
    private final LocalDate utstedtDato;

    @ConstructorProperties({ "terminDato", "utstedtDato" })
    public TerminInfo(LocalDate terminDato, LocalDate utstedtDato) {
        this.terminDato = terminDato;
        this.utstedtDato = utstedtDato;
    }
}
