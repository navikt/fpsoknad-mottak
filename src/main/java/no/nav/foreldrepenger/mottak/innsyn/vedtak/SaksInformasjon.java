package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import java.time.LocalDate;

import lombok.Data;
import no.nav.foreldrepenger.common.domain.felles.BehandlingTema;

@Data
public class SaksInformasjon {
    private String fagsakId;
    private String fagsakAnnenForelderId;
    private String fagsakType;
    private String tema;
    private BehandlingTema behandlingsTema;
    private String ansvarligSaksbehandlerIdent;
    private String ansvarligBeslutterIdent;
    private String behandlendeEnhet;
    private LocalDate søknadsdato;
    private LocalDate klagedato;
    private LocalDate vedtaksdato;
}
