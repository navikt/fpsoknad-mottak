package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;

import lombok.Data;

@Data
public class OppholdsInformasjon {

    private final boolean fødselINorge;
    private final boolean norgeSiste12;
    private final boolean norgeNeste12;

    @ConstructorProperties({ "fødselINorge", "norgeSiste12", "norgeNeste12" })
    public OppholdsInformasjon(boolean fødselINorge, boolean norgeSiste12, boolean norgeNeste12) {
        this.fødselINorge = fødselINorge;
        this.norgeSiste12 = norgeSiste12;
        this.norgeNeste12 = norgeNeste12;
    }

}
