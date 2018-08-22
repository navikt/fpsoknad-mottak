package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import lombok.Data;

@Data
public class FPInfoSakStatus {
    private final String saksnr;
    private final FPInfoFagsakStatus fagsakStatus;
    private final FPInfoFagsakÅrsak fagsakÅrsak;
    private final FPInfoFagsakYtelseType fagsakYtelseType;
    private final String aktørId;
    private final String aktørIdAnnenPart;
    private final String aktørIdBarn;

    public FPInfoSakStatus(String saksnr, FPInfoFagsakStatus fagsakStatus, FPInfoFagsakÅrsak fagsakÅrsak,
            FPInfoFagsakYtelseType fagsakYtelseType, String aktørId, String aktørIdAnnenPart, String aktørIdBarn) {
        this.saksnr = saksnr;
        this.fagsakStatus = fagsakStatus;
        this.fagsakÅrsak = fagsakÅrsak;
        this.fagsakYtelseType = fagsakYtelseType;
        this.aktørId = aktørId;
        this.aktørIdAnnenPart = aktørIdAnnenPart;
        this.aktørIdBarn = aktørIdBarn;
    }

}
