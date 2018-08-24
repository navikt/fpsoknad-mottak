package no.nav.foreldrepenger.mottak.innsending.fpinfo;

public class FPInfoSakStatus {
    private final String saksnummer;
    private final FPInfoFagsakStatus fagsakStatus;
    private final FPInfoFagsakÅrsak fagsakÅrsak;
    private final FPInfoFagsakYtelseType fagsakYtelseType;
    private final String aktørId;
    private final String aktørIdAnnenPart;
    private final String aktørIdBarn;

    public FPInfoSakStatus(String saksnummer, FPInfoFagsakStatus fagsakStatus, FPInfoFagsakÅrsak fagsakÅrsak,
            FPInfoFagsakYtelseType fagsakYtelseType, String aktørId, String aktørIdAnnenPart, String aktørIdBarn) {
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

    public String getAktørIdBarn() {
        return aktørIdBarn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [saksnummer=" + saksnummer + ", fagsakStatus=" + fagsakStatus
                + ", fagsakÅrsak="
                + fagsakÅrsak + ", fagsakYtelseType=" + fagsakYtelseType + ", aktørId=" + aktørId
                + ", aktørIdAnnenPart=" + aktørIdAnnenPart + ", aktørIdBarn=" + aktørIdBarn + "]";
    }

}
