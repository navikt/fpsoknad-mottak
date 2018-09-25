package no.nav.foreldrepenger.lookup.ws.ytelser;

import java.time.LocalDate;
import java.util.Objects;

public class Sak {

    private String sakId;
    private String sakstype;
    private String fagomrade;
    private String fagsystem;
    private String fagsystemSakId;
    private LocalDate opprettet;

    public Sak(String sakId, String sakstype, String fagomrade, String fagsystem,
            String fagsystemSakId, LocalDate opprettet) {
        this.sakId = sakId;
        this.sakstype = sakstype;
        this.fagomrade = fagomrade;
        this.fagsystem = fagsystem;
        this.fagsystemSakId = fagsystemSakId;
        this.opprettet = opprettet;
    }

    public String getSakId() {
        return sakId;
    }

    public String getSakstype() {
        return sakstype;
    }

    public String getFagomrade() {
        return fagomrade;
    }

    public String getFagsystem() {
        return fagsystem;
    }

    public String getFagsystemSakId() {
        return fagsystemSakId;
    }

    public LocalDate getOpprettet() {
        return opprettet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sak sak = (Sak) o;
        return Objects.equals(sakId, sak.sakId) &&
                Objects.equals(sakstype, sak.sakstype) &&
                Objects.equals(fagomrade, sak.fagomrade) &&
                Objects.equals(fagsystem, sak.fagsystem) &&
                Objects.equals(fagsystemSakId, sak.fagsystemSakId) &&
                Objects.equals(opprettet, sak.opprettet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sakId, sakstype, fagomrade, fagsystem, fagsystemSakId, opprettet);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sakId=" + sakId + ", sakstype=" + sakstype + ", fagomrade=" + fagomrade
                + ", fagsystem=" + fagsystem + ", fagsystemSakId=" + fagsystemSakId + ", opprettet=" + opprettet + "]";
    }
}
