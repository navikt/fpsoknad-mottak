package no.nav.foreldrepenger.mottak.oppslag.dkif;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
class DigitalKontaktinfo {

    private static final Logger LOG = LoggerFactory.getLogger(DigitalKontaktinfo.class);
    private final Map<String, Kontaktinformasjon> kontaktinfo;

    public DigitalKontaktinfo(@JsonProperty("kontaktinfo") Map<String, Kontaktinformasjon> kontaktinfo) {
        this.kontaktinfo = kontaktinfo;
    }

    public Målform getMålform(String ident) {
        LOG.trace("Henter målform for {} fra {}", ident, kontaktinfo);
        return Optional.ofNullable(kontaktinfo)
                .map(k -> k.get(ident))
                .map(Kontaktinformasjon::getMålform)
                .orElse(Målform.standard());
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
