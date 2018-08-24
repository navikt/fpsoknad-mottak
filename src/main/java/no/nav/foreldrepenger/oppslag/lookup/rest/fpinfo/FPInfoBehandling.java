package no.nav.foreldrepenger.oppslag.lookup.rest.fpinfo;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import lombok.Data;

@Data
public class FPInfoBehandling {

    private final BehandlingsStatus status;
    private final FPInfoBehandlingsType type;
    private final FPInfoBehandlingsTema tema;
    private final FPInfoBehandlingsÅrsaakType årsak;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final List<String> inntektsmeldinger;

    public FPInfoBehandling(BehandlingsStatus status, FPInfoBehandlingsType type, FPInfoBehandlingsTema tema,
            FPInfoBehandlingsÅrsaakType årsak, String behandlendeEnhet, String behandlendeEnhetNavn,
            List<String> inntektsmeldinger) {
        this.status = status;
        this.type = type;
        this.tema = tema;
        this.årsak = årsak;
        this.behandlendeEnhet = behandlendeEnhet;
        this.behandlendeEnhetNavn = behandlendeEnhetNavn;
        this.inntektsmeldinger = Optional.ofNullable(inntektsmeldinger).orElse(emptyList());
    }

}
