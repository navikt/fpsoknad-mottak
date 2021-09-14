package no.nav.foreldrepenger.mottak.innsyn.dto;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.mottak.innsyn.FagsakStatus;
import no.nav.foreldrepenger.mottak.innsyn.Lenke;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SakDTO {
    private static final String BEHANDLINGER = "behandlinger";
    private final String saksnummer;
    private final FagsakStatus fagsakStatus;
    private final String behandlingTema;
    private final AktørId aktørId;
    private final AktørId aktørIdAnnenPart;
    private final List<AktørId> aktørIdBarna;
    private final List<Lenke> lenker;
    private final LocalDateTime opprettetTidspunkt;
    private final LocalDateTime endretTidspunkt;
    private final boolean mottattEndringssøknad;

    @JsonCreator
    public SakDTO(@JsonProperty("saksnummer") String saksnummer,
            @JsonProperty("fagsakStatus") FagsakStatus fagsakStatus,
            @JsonProperty("behandlingTema") String behandlingTema,
            @JsonProperty("aktørId") AktørId aktørId,
            @JsonProperty("aktørIdAnnenPart") AktørId aktørIdAnnenPart,
            @JsonProperty("aktørIdBarna") List<AktørId> aktørIdBarna,
            @JsonProperty("lenker") List<Lenke> lenker,
            @JsonProperty("opprettetTidspunkt") LocalDateTime opprettetTidspunkt,
            @JsonProperty("endretTidspunkt") LocalDateTime endretTidspunkt,
            @JsonProperty("mottattEndringssøknad") boolean mottattEndringssøknad) {
        this.saksnummer = saksnummer;
        this.fagsakStatus = fagsakStatus;
        this.behandlingTema = behandlingTema;
        this.aktørId = aktørId;
        this.aktørIdAnnenPart = aktørIdAnnenPart;
        this.aktørIdBarna = Optional.ofNullable(aktørIdBarna).orElse(emptyList());
        this.lenker = Optional.ofNullable(lenker).orElse(emptyList());
        this.opprettetTidspunkt = opprettetTidspunkt;
        this.endretTidspunkt = endretTidspunkt;
        this.mottattEndringssøknad = mottattEndringssøknad;
    }

    public List<Lenke> getLenker() {
        return lenker;
    }

    @JsonIgnore
    public List<Lenke> getBehandlingsLenker() {
        return safeStream(getLenker())
                .filter(s -> BEHANDLINGER.equals(s.rel()))
                .toList();
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public FagsakStatus getFagsakStatus() {
        return fagsakStatus;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public AktørId getAktørIdAnnenPart() {
        return aktørIdAnnenPart;
    }

    public List<AktørId> getAktørIdBarna() {
        return aktørIdBarna;
    }

    public String getBehandlingTema() {
        return behandlingTema;
    }

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }

    public boolean isMottattEndringssøknad() {
        return mottattEndringssøknad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SakDTO sakDTO = (SakDTO) o;
        return mottattEndringssøknad == sakDTO.mottattEndringssøknad && Objects.equals(saksnummer, sakDTO.saksnummer)
                && fagsakStatus == sakDTO.fagsakStatus && Objects.equals(behandlingTema, sakDTO.behandlingTema)
                && Objects.equals(aktørId, sakDTO.aktørId) && Objects.equals(aktørIdAnnenPart, sakDTO.aktørIdAnnenPart)
                && Objects.equals(aktørIdBarna, sakDTO.aktørIdBarna) && Objects.equals(lenker, sakDTO.lenker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saksnummer, fagsakStatus, behandlingTema, aktørId, aktørIdAnnenPart, aktørIdBarna, lenker,
                mottattEndringssøknad);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [saksnummer=" + saksnummer + ", fagsakStatus=" + fagsakStatus
                + ", behandlingTema="
                + behandlingTema + ", aktørId=" + aktørId + ", aktørIdAnnenPart=" + aktørIdAnnenPart + ", aktørIdBarna="
                + aktørIdBarna + ", lenker=" + lenker + ", mottattEndringssøknad=" + mottattEndringssøknad + "]";
    }
}
