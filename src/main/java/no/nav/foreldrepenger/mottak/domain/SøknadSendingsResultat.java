package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SøknadSendingsResultat {

    public static final SøknadSendingsResultat OK = new SøknadSendingsResultat();

    private String ref;
    private LocalDateTime mottattDato;

    public SøknadSendingsResultat withReference(String ref) {
        this.ref = ref;
        return this;
    }

    public SøknadSendingsResultat withMottattDato(LocalDateTime mottattDato) {
        this.mottattDato = mottattDato;
        return this;
    }

    public String getRef() {
        return ref;
    }

    public LocalDateTime getMottatt() {
        return mottattDato;
    }
}
