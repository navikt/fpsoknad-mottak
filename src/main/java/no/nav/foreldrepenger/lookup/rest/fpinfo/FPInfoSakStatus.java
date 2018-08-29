package no.nav.foreldrepenger.lookup.rest.fpinfo;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FPInfoSakStatus {
    private final String saksnummer;
    private final FPInfoFagsakStatus fagsakStatus;
    private final String behandlingsTema;
    private final String aktørId;
    private final String aktørIdAnnenPart;
    private final List<String> aktørIdBarn;

    @JsonCreator
    public FPInfoSakStatus(@JsonProperty("saksnummer") String saksnummer,
            @JsonProperty("fagsakStatus") FPInfoFagsakStatus fagsakStatus,
            @JsonProperty("behandlingsTema") String behandlingsTema,
            @JsonProperty("aktørId") String aktørId,
            @JsonProperty("aktørIdAnnenPart") String aktørIdAnnenPart,
            @JsonProperty("aktørIdBarn") List<String> aktørIdBarn) {
        this.saksnummer = saksnummer;
        this.fagsakStatus = fagsakStatus;
        this.behandlingsTema = behandlingsTema;
        this.aktørId = aktørId;
        this.aktørIdAnnenPart = aktørIdAnnenPart;
        this.aktørIdBarn = Optional.ofNullable(aktørIdBarn).orElse(emptyList());
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public FPInfoFagsakStatus getFagsakStatus() {
        return fagsakStatus;
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getAktørIdAnnenPart() {
        return aktørIdAnnenPart;
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId, aktørIdAnnenPart, aktørIdBarn, fagsakStatus, behandlingsTema,
                saksnummer);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FPInfoSakStatus other = (FPInfoSakStatus) obj;
        if (aktørId == null) {
            if (other.aktørId != null)
                return false;
        }
        else if (!aktørId.equals(other.aktørId))
            return false;
        if (aktørIdAnnenPart == null) {
            if (other.aktørIdAnnenPart != null)
                return false;
        }
        else if (!aktørIdAnnenPart.equals(other.aktørIdAnnenPart))
            return false;
        if (aktørIdBarn == null) {
            if (other.aktørIdBarn != null)
                return false;
        }
        else if (!aktørIdBarn.equals(other.aktørIdBarn))
            return false;
        if (fagsakStatus != other.fagsakStatus)
            return false;
        if (behandlingsTema != other.behandlingsTema)
            return false;

        if (saksnummer == null) {
            if (other.saksnummer != null)
                return false;
        }
        else if (!saksnummer.equals(other.saksnummer))
            return false;
        return true;
    }

    public List<String> getAktørIdBarn() {
        return aktørIdBarn;
    }

    @Override
    public String toString() {
        return "FPInfoSakStatus [saksnummer=" + saksnummer + ", fagsakStatus=" + fagsakStatus + ", behandlingsTema="
                + behandlingsTema + ", aktørId=" + aktørId + ", aktørIdAnnenPart=" + aktørIdAnnenPart + ", aktørIdBarn="
                + aktørIdBarn + "]";
    }

}
