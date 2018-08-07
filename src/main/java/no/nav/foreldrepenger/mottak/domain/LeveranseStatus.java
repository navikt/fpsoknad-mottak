package no.nav.foreldrepenger.mottak.domain;

public enum LeveranseStatus {
    INNVLIGET("behandling_innvilget"), AVSLÅTT("behandling_avslått"), PÅGÅR("behandling_pågar"), PÅ_VENT(
            "behandling_på_vent"), SENDT_OG_FORSØKT_BEHANDLET_FPSAK(), IKKE_SENDT_FPSAK, FP_FORDEL_MESSED_UP;

    private final String melding;

    private LeveranseStatus() {
        this("NA");
    }

    private LeveranseStatus(String melding) {
        this.melding = melding;
    }
}
