package no.nav.foreldrepenger.mottak.innsyn.mappers;

import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

public abstract class AbstractXMLMapper implements XMLSøknadMapper {

    protected static final String UKJENT_KODEVERKSVERDI = "-";

    protected final Oppslag oppslag;

    public AbstractXMLMapper(Oppslag oppslag) {
        this.oppslag = oppslag;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + "]";
    }
}
