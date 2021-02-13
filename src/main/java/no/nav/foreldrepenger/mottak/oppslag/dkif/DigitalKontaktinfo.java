package no.nav.foreldrepenger.mottak.oppslag.dkif;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DigitalKontaktinfo(Map<String, Kontaktinformasjon> kontaktinfo) {

    public Målform getMålform(String ident) {
        return Optional.ofNullable(kontaktinfo)
                .map(k -> k.get(ident))
                .map(Kontaktinformasjon::målform)
                .orElse(Målform.standard());
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Kontaktinformasjon(@JsonProperty("spraak") Målform målform) {
}
