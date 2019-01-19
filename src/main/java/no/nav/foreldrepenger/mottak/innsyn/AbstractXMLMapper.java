package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.VersjonsBevisst;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

public abstract class AbstractXMLMapper implements XMLMapper, VersjonsBevisst {

    protected final Oppslag oppslag;
    private final SøknadInspektør inspektør;

    public AbstractXMLMapper(Oppslag oppslag, SøknadInspektør inspektør) {
        this.oppslag = oppslag;
        this.inspektør = inspektør;
    }

    protected SøknadType type(String xml) {
        return inspektør.type(xml);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", inspektør=" + inspektør + "]";
    }
}
