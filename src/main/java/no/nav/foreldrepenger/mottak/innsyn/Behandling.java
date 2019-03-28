package no.nav.foreldrepenger.mottak.innsyn;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.BehandlingsTema;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;

@Data
@Builder
public class Behandling {
    private final LocalDateTime opprettetTidspunkt;
    private final LocalDateTime endretTidspunkt;
    private final String status;
    private final String type;
    private final BehandlingsTema tema;
    private final String årsak;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final String behandlingResultat;
    private final List<String> inntektsmeldinger;
    private final InnsynsSøknad søknad;
    private final Vedtak vedtak;

}
