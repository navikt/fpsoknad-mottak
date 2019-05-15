package no.nav.foreldrepenger.mottak.innsyn.uttaksplan;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ArbeidsgiverInfo {
    private final String id;
    private final String navn;
    private final ArbeidsgiverType type;
    
    public ArbeidsgiverInfo(@JsonProperty("id") String id, @JsonProperty("type") ArbeidsgiverType type, @JsonProperty("navn") String navn) {
        this.id = id;
        this.type = type;
        this.navn = navn;
    }
}
