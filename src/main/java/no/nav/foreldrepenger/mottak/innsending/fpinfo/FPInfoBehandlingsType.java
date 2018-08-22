package no.nav.foreldrepenger.mottak.innsending.fpinfo;

public enum FPInfoBehandlingsType {
    BT002("BT-002","Førstegangssøknad"),
    BT003("BT-002","Klage"),
    BT004("BT-004","Revurdering"),
    BT006("BT-006","Innsyn");


    private final String beskrivelse;
    private final String navn;

    FPInfoBehandlingsType(String navn,String beskrivelse) {
        this.beskrivelse = beskrivelse;
        this.navn = navn;
    }
}
