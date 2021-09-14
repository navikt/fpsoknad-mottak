package no.nav.foreldrepenger.mottak.innsyn;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import no.nav.foreldrepenger.common.domain.felles.BehandlingTema;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;

@Data
@Builder
public class Behandling {
    private final LocalDateTime opprettetTidspunkt;
    private final LocalDateTime endretTidspunkt;
    private final BehandlingStatus status;
    private final BehandlingType type;
    private final BehandlingTema tema;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final BehandlingResultat behandlingResultat;
    private final List<String> inntektsmeldinger;
    private final InnsynsSøknad søknad;
    private final Vedtak vedtak;

}
