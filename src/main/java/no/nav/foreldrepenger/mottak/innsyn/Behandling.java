package no.nav.foreldrepenger.mottak.innsyn;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Behandling {
    private final LocalDateTime opprettetTidspunkt;
    private final LocalDateTime endretTidspunkt;
    private final String status;
    private final String type;
    private final String tema;
    private final String årsak;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final String behandlingResultatType;
    private final List<String> inntektsmeldinger;
    private final InnsynsSøknad søknad;

}
