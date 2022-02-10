package no.nav.foreldrepenger.mottak.innsyn.dto;

import static java.util.Collections.emptyList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BehandlingDTO {
    private final String status;
    private final String type;
    private final String tema;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final String behandlingResultat;
    private final LocalDateTime opprettetTidspunkt;
    private final LocalDateTime endretTidspunkt;
    private final List<String> inntektsmeldinger;

    @JsonCreator
    public BehandlingDTO(@JsonProperty("opprettetTidspunkt") LocalDateTime opprettetTidspunkt,
            @JsonProperty("endretTidspunkt") LocalDateTime endretTidspunkt,
            @JsonProperty("status") String status,
            @JsonProperty("type") String type,
            @JsonProperty("tema") String tema,
            @JsonProperty("behandlendeEnhet") String behandlendeEnhet,
            @JsonProperty("behandlendeEnhetNavn") String behandlendeEnhetNavn,
            @JsonProperty("behandlingResultat") String behandlingResultat,
            @JsonProperty("inntektsmeldinger") List<String> inntektsmeldinger) {
        this.opprettetTidspunkt = opprettetTidspunkt;
        this.endretTidspunkt = endretTidspunkt;
        this.status = status;
        this.tema = tema;
        this.type = type;
        this.behandlendeEnhet = behandlendeEnhet;
        this.behandlendeEnhetNavn = behandlendeEnhetNavn;
        this.behandlingResultat = behandlingResultat;
        this.inntektsmeldinger = Optional.ofNullable(inntektsmeldinger).orElse(emptyList());
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getTema() {
        return tema;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public String getBehandlingResultat() {
        return behandlingResultat;
    }

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }

    public List<String> getInntektsmeldinger() {
        return inntektsmeldinger;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [status=" + status + ", type=" + type + ", tema=" + tema
                + ", behandlendeEnhet=" + behandlendeEnhet + ", behandlendeEnhetNavn=" + behandlendeEnhetNavn
                + ", behandlingResultat=" + behandlingResultat + ", opprettetTidspunkt=" + opprettetTidspunkt
                + ", endretTidspunkt=" + endretTidspunkt + ", inntektsmeldinger=" + inntektsmeldinger + "]";
    }
}
