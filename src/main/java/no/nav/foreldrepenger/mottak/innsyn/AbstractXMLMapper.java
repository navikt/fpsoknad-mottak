package no.nav.foreldrepenger.mottak.innsyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

public abstract class AbstractXMLMapper implements XMLMapper, VersjonsBevisst {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractXMLMapper.class);
    protected final Oppslag oppslag;
    private final SøknadInspektør inspektør;

    public AbstractXMLMapper(Oppslag oppslag, SøknadInspektør inspektør) {
        this.oppslag = oppslag;
        this.inspektør = inspektør;
    }

    protected SøknadType type(String xml) {
        SøknadEgenskaper resultat = inspektør.inspiser(xml);
        LOG.info("Dette er en søknad av type {} og versjon {}", resultat.getType(), resultat.getVersjon());
        return resultat.getType();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon = " + versjon() + ", oppslag=" + oppslag + ", inspektør="
                + inspektør + "]";
    }

}
