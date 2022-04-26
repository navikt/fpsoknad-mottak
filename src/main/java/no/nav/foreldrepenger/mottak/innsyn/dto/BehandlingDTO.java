package no.nav.foreldrepenger.mottak.innsyn.dto;

import static java.util.Collections.emptyList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record BehandlingDTO(String status,
                            String type,
                            String tema,
                            String behandlendeEnhet,
                            String behandlendeEnhetNavn,
                            String behandlingResultat,
                            LocalDateTime opprettetTidspunkt,
                            LocalDateTime endretTidspunkt,
                            List<String> inntektsmeldinger) {

    public BehandlingDTO {
        inntektsmeldinger = Optional.ofNullable(inntektsmeldinger).orElse(emptyList());
    }
}
