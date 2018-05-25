package no.nav.foreldrepenger.mottak.fpfordel;

public class FPSakFordeltKvittering extends FPFordelKvittering {

    static final String STATUS = "FPSAK";

    private final String jounalId;
    private final String saksnummer;

    public String getJounalId() {
        return jounalId;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public FPSakFordeltKvittering(String jounalId, String saksnummer) {
        super(STATUS);
        this.jounalId = jounalId;
        this.saksnummer = saksnummer;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [jounalId=" + jounalId + ", saksnummer=" + saksnummer + "]";
    }
}
