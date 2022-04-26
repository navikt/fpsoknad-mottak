package no.nav.foreldrepenger.mottak.innsyn.dto;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.innsyn.FagsakStatus;

public record SakDTO(String saksnummer,
                     FagsakStatus fagsakStatus,
                     String behandlingTema,
                     AktørId aktørId,
                     AktørId aktørIdAnnenPart,
                     List<AktørId> aktørIdBarna,
                     List<LenkeDTO> lenker,
                     LocalDateTime opprettetTidspunkt,
                     LocalDateTime endretTidspunkt,
                     boolean mottattEndringssøknad) {

    private static final String BEHANDLINGER = "behandlinger";

    public SakDTO {
        aktørIdBarna = Optional.ofNullable(aktørIdBarna).orElse(emptyList());
        lenker = Optional.ofNullable(lenker).orElse(emptyList());
    }

    @JsonIgnore
    public List<LenkeDTO> behandlingsLenker() {
        return safeStream(lenker())
            .filter(s -> BEHANDLINGER.equals(s.rel()))
            .toList();
    }
}
