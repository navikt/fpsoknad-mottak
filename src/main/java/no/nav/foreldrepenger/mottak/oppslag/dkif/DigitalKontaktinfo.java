package no.nav.foreldrepenger.mottak.oppslag.dkif;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DigitalKontaktinfo {

    private final Map<String, Kontaktinformasjon> kontaktinfo;

    public DigitalKontaktinfo(@JsonProperty("kontaktinfo") Map<String, Kontaktinformasjon> kontaktinfo) {
        this.kontaktinfo = kontaktinfo;
    }

    public Målform getMålform(String ident) {
        return Optional.ofNullable(kontaktinfo)
                .map(k -> k.get(ident))
                .map(Kontaktinformasjon::getMålform)
                .orElse(Målform.NB);
    }

    @EqualsAndHashCode
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Kontaktinformasjon {

        private final Målform målform;

        Kontaktinformasjon(@JsonProperty("spraak") Målform målform) {
            this.målform = målform;
        }

        private Målform getMålform() {
            return målform;
        }
    }
}
