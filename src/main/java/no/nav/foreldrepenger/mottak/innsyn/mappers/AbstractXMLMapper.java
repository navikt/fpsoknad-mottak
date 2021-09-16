package no.nav.foreldrepenger.mottak.innsyn.mappers;

import no.nav.foreldrepenger.common.oppslag.Oppslag;

public abstract class AbstractXMLMapper implements XMLSøknadMapper {
    protected final Oppslag oppslag;

    public AbstractXMLMapper(Oppslag oppslag) {
        this.oppslag = oppslag;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + "]";
    }
}
