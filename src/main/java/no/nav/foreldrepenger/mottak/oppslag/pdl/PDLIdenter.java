package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class PDLIdenter {
    private final Set<PDLIdentInformasjon> identer;

    @JsonCreator
    public PDLIdenter(@JsonProperty("identer") Set<PDLIdentInformasjon> identer) {
        this.identer = identer;
    }

}
