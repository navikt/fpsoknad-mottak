package no.nav.foreldrepenger.mottak.oppslag.dkif;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.common.oppslag.dkif.Målform;

public record Kontaktinformasjon(@JsonProperty("spraak") Målform målform) {
}
