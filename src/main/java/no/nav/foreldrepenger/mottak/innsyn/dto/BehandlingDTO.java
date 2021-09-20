package no.nav.foreldrepenger.mottak.innsyn.dto;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.innsyn.Lenke;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingDTO {
    private static final String SØKNAD = "søknad";
    private static final String VEDTAK = "vedtak";
    private final String status;
    private final String type;
    private final String tema;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final String behandlingResultat;
    private final LocalDateTime opprettetTidspunkt;
    private final LocalDateTime endretTidspunkt;
    private final List<String> inntektsmeldinger;
    private final List<Lenke> lenker;

    @JsonCreator
    public BehandlingDTO(@JsonProperty("opprettetTidspunkt") LocalDateTime opprettetTidspunkt,
            @JsonProperty("endretTidspunkt") LocalDateTime endretTidspunkt,
            @JsonProperty("status") String status,
            @JsonProperty("type") String type,
            @JsonProperty("tema") String tema,
            @JsonProperty("behandlendeEnhet") String behandlendeEnhet,
            @JsonProperty("behandlendeEnhetNavn") String behandlendeEnhetNavn,
            @JsonProperty("behandlingResultat") String behandlingResultat,
            @JsonProperty("inntektsmeldinger") List<String> inntektsmeldinger,
            @JsonProperty("lenker") List<Lenke> lenker) {
        this.opprettetTidspunkt = opprettetTidspunkt;
        this.endretTidspunkt = endretTidspunkt;
        this.status = status;
        this.tema = tema;
        this.type = type;
        this.behandlendeEnhet = behandlendeEnhet;
        this.behandlendeEnhetNavn = behandlendeEnhetNavn;
        this.behandlingResultat = behandlingResultat;
        this.inntektsmeldinger = Optional.ofNullable(inntektsmeldinger).orElse(emptyList());
        this.lenker = Optional.ofNullable(lenker).orElse(emptyList());
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

    public List<Lenke> getLenker() {
        return lenker;
    }

    public Lenke getSøknadsLenke() {
        return getLenke(SØKNAD);
    }

    public Lenke getVedtaksLenke() {
        return getLenke(VEDTAK);
    }

    private Lenke getLenke(String type) {
        return safeStream(getLenker())
                .filter(s -> s.rel().equals(type))
                .findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [status=" + status + ", type=" + type + ", tema=" + tema
                + ", behandlendeEnhet=" + behandlendeEnhet + ", behandlendeEnhetNavn=" + behandlendeEnhetNavn
                + ", behandlingResultat=" + behandlingResultat + ", opprettetTidspunkt=" + opprettetTidspunkt
                + ", endretTidspunkt=" + endretTidspunkt + ", inntektsmeldinger=" + inntektsmeldinger + ", lenker=" + lenker
                + "]";
    }
}
