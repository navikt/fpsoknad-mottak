package no.nav.foreldrepenger.mottak.oppslag.dkif;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;

import java.util.Map;

public record Kontaktinformasjoner(Map<Fødselsnummer, Kontaktinformasjon> personer, Map<Fødselsnummer, FeilKode> feil) {

    public record Kontaktinformasjon(boolean aktiv, Målform spraak) {
    }

    public enum FeilKode {
        person_ikke_funnet,
        skjermet,
        fortrolig_adresse,
        strengt_fortrolig_adresse,
        strengt_fortrolig_utenlandsk_adresse,
        noen_andre
    }
}