package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import java.util.List;

import lombok.Data;

@Data
public class FPInfoBehandling {

    private final BehandlingsStatus status;
    private final FPInfoBehandlingsType type;
    private final FPInfoBehandlingsType tema;
    private final FPInfoBehandlingsÅrsaakType årsak;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final List<String> inntektsmeldinger;

}
