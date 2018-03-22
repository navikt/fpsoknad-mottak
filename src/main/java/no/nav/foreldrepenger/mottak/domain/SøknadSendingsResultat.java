package no.nav.foreldrepenger.mottak.domain;

import lombok.Data;

@Data
public class SøknadSendingsResultat {

    public static final SøknadSendingsResultat OK = new SøknadSendingsResultat();

    private String ref;

    public SøknadSendingsResultat withReference(String ref) {
        this.ref = ref;
        return this;
    }

    public String getRef() {
        return ref;
    }

}
