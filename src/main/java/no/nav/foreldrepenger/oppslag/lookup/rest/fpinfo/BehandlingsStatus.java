package no.nav.foreldrepenger.oppslag.lookup.rest.fpinfo;

public enum BehandlingsStatus {
    UTRED("Utredes"), AVSLU("Avsluttet"), FVED("Fatter vedta"), IVED("Iverksetter vedtak"), OPPRE("Opprettet");

    private final String beskrivelse;

    BehandlingsStatus(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

}
