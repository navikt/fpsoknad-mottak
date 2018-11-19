package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SakWrapper {
    private static final String BEHANDLINGER = "behandlinger";
    private final String saksnummer;
    private final FagsakStatus fagsakStatus;
    private final String behandlingTema;
    private final String aktørId;
    private final String aktørIdAnnenPart;
    private final List<String> aktørIdBarna;
    private final List<Lenke> lenker;
    private final LocalDateTime opprettetTidspunkt;
    private final LocalDateTime endretTidspunkt;

    @JsonCreator
    public SakWrapper(@JsonProperty("saksnummer") String saksnummer,
                      @JsonProperty("fagsakStatus") FagsakStatus fagsakStatus,
                      @JsonProperty("behandlingTema") String behandlingTema,
                      @JsonProperty("aktørId") String aktørId,
                      @JsonProperty("aktørIdAnnenPart") String aktørIdAnnenPart,
                      @JsonProperty("aktørIdBarna") List<String> aktørIdBarna,
                      @JsonProperty("lenker") List<Lenke> lenker,
                      @JsonProperty("opprettetTidspunkt") LocalDateTime opprettetTidspunkt,
                      @JsonProperty("endretTidspunkt") LocalDateTime endretTidspunkt) {
        this.saksnummer = saksnummer;
        this.fagsakStatus = fagsakStatus;
        this.behandlingTema = behandlingTema;
        this.aktørId = aktørId;
        this.aktørIdAnnenPart = aktørIdAnnenPart;
        this.aktørIdBarna = Optional.ofNullable(aktørIdBarna).orElse(emptyList());
        this.lenker = Optional.ofNullable(lenker).orElse(emptyList());
        this.opprettetTidspunkt = opprettetTidspunkt;
        this.endretTidspunkt = endretTidspunkt;
    }

    public List<Lenke> getLenker() {
        return lenker;
    }

    @JsonIgnore
    public List<Lenke> getBehandlingsLenker() {
        return getLenker().stream().filter(s -> s.getRel().equals(BEHANDLINGER)).collect(toList());
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public FagsakStatus getFagsakStatus() {
        return fagsakStatus;
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getAktørIdAnnenPart() {
        return aktørIdAnnenPart;
    }

    public List<String> getAktørIdBarna() {
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

    @Override
    public int hashCode() {
        return Objects.hash(aktørId, aktørIdAnnenPart, aktørIdBarna, fagsakStatus, behandlingTema,
            saksnummer, lenker);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SakWrapper other = (SakWrapper) obj;
        if (aktørId == null) {
            if (other.aktørId != null)
                return false;
        } else if (!aktørId.equals(other.aktørId))
            return false;
        if (aktørIdAnnenPart == null) {
            if (other.aktørIdAnnenPart != null)
                return false;
        } else if (!aktørIdAnnenPart.equals(other.aktørIdAnnenPart))
            return false;
        if (aktørIdBarna == null) {
            if (other.aktørIdBarna != null)
                return false;
        } else if (!aktørIdBarna.equals(other.aktørIdBarna))
            return false;
        if (fagsakStatus != other.fagsakStatus)
            return false;
        if (!behandlingTema.equals(other.behandlingTema))
            return false;

        if (saksnummer == null) {
            return other.saksnummer == null;
        } else {
            return saksnummer.equals(other.saksnummer);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [saksnummer=" + saksnummer + ", fagsakStatus=" + fagsakStatus
            + ", behandlingTema="
            + behandlingTema + ", aktørId=" + aktørId + ", aktørIdAnnenPart=" + aktørIdAnnenPart + ", aktørIdBarna="
            + aktørIdBarna + ", lenker=" + lenker + "]";
    }

}
