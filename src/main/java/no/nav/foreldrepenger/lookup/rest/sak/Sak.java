package no.nav.foreldrepenger.lookup.rest.sak;

import java.time.LocalDate;
import java.util.Objects;

public class Sak {

    private String sakId;
    private String sakstype;
    private String fagsystem;
    private String saksnummer;
    private String status;
    private LocalDate opprettet;
    private String opprettetAv;

    public Sak(String sakId, String sakstype, String fagsystem, String saksnummer, String status, LocalDate opprettet, String opprettetAv) {
        this.sakId = sakId;
        this.sakstype = sakstype;
        this.fagsystem = fagsystem;
        this.saksnummer = saksnummer;
        this.status = status;
        this.opprettet = opprettet;
        this.opprettetAv = opprettetAv;
    }

    public String getSakId() {
        return sakId;
    }

    public String getSakstype() {
        return sakstype;
    }

    public String getFagsystem() {
        return fagsystem;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public LocalDate getOpprettet() {
        return opprettet;
    }

    public String getStatus() {
        return status;
    }

    public String getOpprettetAv() { return opprettetAv; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sak sak = (Sak) o;
        return Objects.equals(sakId, sak.sakId) &&
            Objects.equals(sakstype, sak.sakstype) &&
            Objects.equals(fagsystem, sak.fagsystem) &&
            Objects.equals(saksnummer, sak.saksnummer) &&
            Objects.equals(opprettet, sak.opprettet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sakId, sakstype, fagsystem, saksnummer, opprettet);
    }

    @Override
    public String toString() {
        return "Sak{" +
            "sakId='" + sakId + '\'' +
            ", sakstype='" + sakstype + '\'' +
            ", fagsystem='" + fagsystem + '\'' +
            ", saksnummer='" + saksnummer + '\'' +
            ", opprettet=" + opprettet +
            ", opprettetAv=" + opprettetAv +
            '}';
    }
}
