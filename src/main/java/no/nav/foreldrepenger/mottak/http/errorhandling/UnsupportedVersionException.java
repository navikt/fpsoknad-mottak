package no.nav.foreldrepenger.mottak.http.errorhandling;

import static no.nav.foreldrepenger.mottak.util.Versjon.alleNameapaces;

import no.nav.foreldrepenger.mottak.util.Versjon;

public class UnsupportedVersionException extends VersionException {

    public UnsupportedVersionException(Versjon versjon) {
        super(versjon);
    }

    public UnsupportedVersionException(String namespace) {
        super("Namespace " + namespace + " er ikke støttet. lovlige verdier er " + alleNameapaces());
    }
}
