package no.nav.foreldrepenger.mottak.domain;

public enum LeveranseStatus {
    PÅ_VENT,
    AVSLÅTT,
    PÅGÅR,
    INNVILGET,
    SENDT_OG_FORSØKT_BEHANDLET_FPSAK,
    IKKE_SENDT_FPSAK,
    FP_FORDEL_MESSED_UP,
    GOSYS;

    public boolean erVellykket() {
        return this.equals(PÅGÅR) || this.equals(PÅ_VENT) || this.equals(INNVILGET) ||
            this.equals(SENDT_OG_FORSØKT_BEHANDLET_FPSAK) || this.equals(GOSYS);

    }
}


