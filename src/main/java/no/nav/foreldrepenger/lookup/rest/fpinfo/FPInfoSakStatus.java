package no.nav.foreldrepenger.lookup.rest.fpinfo;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FPInfoSakStatus {
    private final String saksnummer;
    private final FPInfoFagsakStatus fagsakStatus;
    private final FPInfoFagsakÅrsak fagsakÅrsak;
    private final FPInfoFagsakYtelseType fagsakYtelseType;
    private final String aktørId;
    private final String aktørIdAnnenPart;
    private final String aktørIdBarn;

    @JsonCreator
    public FPInfoSakStatus(@JsonProperty("saksnummer") String saksnummer,
            @JsonProperty("fagsakStatus") FPInfoFagsakStatus fagsakStatus,
            @JsonProperty("fagsakÅrsak") FPInfoFagsakÅrsak fagsakÅrsak,
            @JsonProperty("fagsakYtelseType") FPInfoFagsakYtelseType fagsakYtelseType,
            @JsonProperty("aktørId") String aktørId,
            @JsonProperty("aktørIdAnnenPart") String aktørIdAnnenPart,
            @JsonProperty("aktørIdBarn") String aktørIdBarn) {
        this.saksnummer = saksnummer;
        this.fagsakStatus = fagsakStatus;
        this.fagsakÅrsak = fagsakÅrsak;
        this.fagsakYtelseType = fagsakYtelseType;
        this.aktørId = aktørId;
        this.aktørIdAnnenPart = aktørIdAnnenPart;
        this.aktørIdBarn = aktørIdBarn;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public FPInfoFagsakStatus getFagsakStatus() {
        return fagsakStatus;
    }

    public FPInfoFagsakÅrsak getFagsakÅrsak() {
        return fagsakÅrsak;
    }

    public FPInfoFagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getAktørIdAnnenPart() {
        return aktørIdAnnenPart;
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId, aktørIdAnnenPart, aktørIdBarn, fagsakStatus, fagsakYtelseType, fagsakÅrsak,
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
        if (fagsakYtelseType != other.fagsakYtelseType)
            return false;
        if (fagsakÅrsak != other.fagsakÅrsak)
            return false;
        if (saksnummer == null) {
            if (other.saksnummer != null)
                return false;
        }
        else if (!saksnummer.equals(other.saksnummer))
            return false;
        return true;
    }

    public String getAktørIdBarn() {
        return aktørIdBarn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [saksnummeret=" + saksnummer + ", fagsakStatus=" + fagsakStatus
                + ", fagsakÅrsak="
                + fagsakÅrsak + ", fagsakYtelseType=" + fagsakYtelseType + ", aktørId=" + aktørId
                + ", aktørIdAnnenPart=" + aktørIdAnnenPart + ", aktørIdBarn=" + aktørIdBarn + "]";
    }

}
