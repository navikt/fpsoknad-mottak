package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.util.Arrays;

enum AvslåttÅrsak implements ResultatÅrsak {

    IKKE_OK("TODO");

    private final String kode;

    AvslåttÅrsak(String kode) {
        this.kode = kode;
    }

    static boolean contains(String periodeResultatÅrsak) {
        return Arrays.stream(values()).anyMatch(v -> periodeResultatÅrsak.equals(v.kode()));
    }

    static AvslåttÅrsak fraKode(String kode) {
        return Arrays.stream(values()).filter(å -> å.kode.equals(kode)).findFirst().orElseThrow();
    }

    @Override
    public String kode() {
        return kode;
    }
}
