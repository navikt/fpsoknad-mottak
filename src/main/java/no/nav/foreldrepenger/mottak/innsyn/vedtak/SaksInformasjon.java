package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import java.time.LocalDate;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.BehandlingsTema;

@Data
public class SaksInformasjon {
    public String fagsakId;
    public String fagsakAnnenForelderId;
    public String fagsakType;
    public String tema;
    public BehandlingsTema behandlingsTema;
    public String ansvarligSaksbehandlerIdent;
    public String ansvarligBeslutterIdent;
    public String behandlendeEnhet;
    public LocalDate søknadsdato;
    public LocalDate klagedato;
    public LocalDate vedtaksdato;
}
