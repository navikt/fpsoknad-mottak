package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.AktorId;

@Data
public class FPInfoSakStatus {
    private final String saksnr;
    private final FPInfoFagsakStatus fagsakStatus;
    private final FPInfoFagsakÅrsak fagsakÅrsak;
    private final FPInfoFagsakYtelseType fagsakYtelseType;
    private final AktorId aktørId;
    private final AktorId aktørIdAnnenPart;
    private final AktorId aktørIdBarn;

    public FPInfoSakStatus(String saksnr, FPInfoFagsakStatus fagsakStatus, FPInfoFagsakÅrsak fagsakÅrsak,
            FPInfoFagsakYtelseType fagsakYtelseType, AktorId aktørId, AktorId aktørIdAnnenPart, AktorId aktørIdBarn) {
        this.saksnr = saksnr;
        this.fagsakStatus = fagsakStatus;
        this.fagsakÅrsak = fagsakÅrsak;
        this.fagsakYtelseType = fagsakYtelseType;
        this.aktørId = aktørId;
        this.aktørIdAnnenPart = aktørIdAnnenPart;
        this.aktørIdBarn = aktørIdBarn;
    }

}
