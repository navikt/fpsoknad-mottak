package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;

import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.SøknadInspektør;
import no.nav.foreldrepenger.mottak.util.VersjonsBevisst;

public abstract class AbstractXMLMapper implements XMLMapper, VersjonsBevisst {

    protected static final String UKJENT_KODEVERKSVERDI = "-";
    protected final Oppslag oppslag;
    private final SøknadInspektør analysator;

    public AbstractXMLMapper(Oppslag oppslag, SøknadInspektør analysator) {
        this.oppslag = oppslag;
        this.analysator = analysator;
    }

    public boolean erEndring(String xml) {
        return ENDRING.equals(analysator.inspiser(xml).type());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon = " + versjon() + ", oppslag=" + oppslag + ", analysator="
                + analysator + "]";
    }

}
