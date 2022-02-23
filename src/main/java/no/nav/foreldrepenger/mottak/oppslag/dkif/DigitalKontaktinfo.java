package no.nav.foreldrepenger.mottak.oppslag.dkif;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.common.oppslag.dkif.Målform;

public record DigitalKontaktinfo(Map<String, Kontaktinformasjon> kontaktinfo) {

    public Målform getMålform(String ident) {
        return Optional.ofNullable(kontaktinfo)
                .map(k -> k.get(ident))
                .map(Kontaktinformasjon::målform)
                .orElse(Målform.standard());
    }
}

record Kontaktinformasjon(@JsonProperty("spraak") Målform målform) {
}
