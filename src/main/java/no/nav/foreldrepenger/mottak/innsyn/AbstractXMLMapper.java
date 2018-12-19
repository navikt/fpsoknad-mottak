package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.SøknadInspektør;
import no.nav.foreldrepenger.mottak.util.VersjonsBevisst;

public abstract class AbstractXMLMapper implements XMLMapper, VersjonsBevisst {

    protected static final String UKJENT_KODEVERKSVERDI = "-";
    protected final Oppslag oppslag;
    private final SøknadInspektør inspektør;

    public AbstractXMLMapper(Oppslag oppslag, SøknadInspektør inspektør) {
        this.oppslag = oppslag;
        this.inspektør = inspektør;
    }

    protected SøknadType type(no.nav.vedtak.felles.xml.soeknad.v1.Soeknad søknad) {
        return inspektør.type(søknad);
    }

    protected SøknadType type(no.nav.vedtak.felles.xml.soeknad.v2.Soeknad søknad) {
        return inspektør.type(søknad);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon = " + versjon() + ", oppslag=" + oppslag + ", inspektør="
                + inspektør + "]";
    }

}
