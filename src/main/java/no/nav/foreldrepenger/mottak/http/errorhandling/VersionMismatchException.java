package no.nav.foreldrepenger.mottak.http.errorhandling;

import no.nav.foreldrepenger.mottak.util.Versjon;

public class VersionMismatchException extends VersionException {

    public VersionMismatchException(String name, Object verdi, Versjon v) {
        this(name, verdi, v, null);
    }

    public VersionMismatchException(String name, Object verdi, Versjon v, String lovligeVerdier) {
        super(txt(name, verdi, v, lovligeVerdier), v);
    }

    private static String txt(String name, Object verdi, Versjon v, String lovligeVerdier) {
        if (lovligeVerdier == null) {
            return "Element " + name + " med  verdi " + verdi + " er ikke støttet for versjon " + v.name();
        }
        return "Element " + name + " med  verdi " + verdi + " er ikke støttet for versjon " + v.name()
                + ", lovlige verdier er " + lovligeVerdier;
    }
}
