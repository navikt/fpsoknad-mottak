package no.nav.foreldrepenger.mottak.domain;

import static java.util.Collections.emptyList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenPart;
import no.nav.foreldrepenger.mottak.innsyn.Behandling;
import no.nav.foreldrepenger.mottak.innsyn.FagsakStatus;

@Data
public class Sak {
    private final String saksnummer;
    private final FagsakStatus fagsakStatus;
    private final String behandlingTema;
    private final String aktørId;
    private final AnnenPart annenPart;
    private final List<String> aktørIdBarn;
    private final List<Behandling> behandlinger;
    private final LocalDateTime opprettet;
    private final LocalDateTime endret;

    @JsonCreator
    public Sak(@JsonProperty("saksnummer") String saksnummer,
            @JsonProperty("status") FagsakStatus fagsakStatus,
            @JsonProperty("behandlingTema") String behandlingTema,
            @JsonProperty("aktørId") String aktørId,
            @JsonProperty("fnrAnnenPart") AnnenPart annenPart,
            @JsonProperty("aktørIdBarn") List<String> aktørIdBarn,
            @JsonProperty("behandlinger") List<Behandling> behandlinger,
            @JsonProperty("opprettet") LocalDateTime opprettet,
            @JsonProperty("endret") LocalDateTime endret) {
        this.saksnummer = saksnummer;
        this.fagsakStatus = fagsakStatus;
        this.behandlingTema = behandlingTema;
        this.aktørId = aktørId;
        this.annenPart = annenPart;
        this.aktørIdBarn = Optional.ofNullable(aktørIdBarn).orElse(emptyList());
        this.behandlinger = Optional.ofNullable(behandlinger).orElse(emptyList());
        this.opprettet = opprettet;
        this.endret = endret;
    }
}
