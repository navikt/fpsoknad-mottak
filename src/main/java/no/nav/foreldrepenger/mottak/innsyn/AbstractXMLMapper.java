package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

public abstract class AbstractXMLMapper implements XMLMapper {

    protected final Oppslag oppslag;

    public AbstractXMLMapper(Oppslag oppslag) {
        this.oppslag = oppslag;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + "]";
    }
}
