package no.nav.foreldrepenger.mottak.innsyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.SøknadInspektør;
import no.nav.foreldrepenger.mottak.util.VersjonsBevisst;

public abstract class AbstractXMLMapper implements XMLMapper, VersjonsBevisst {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractXMLMapper.class);
    protected final Oppslag oppslag;
    private final SøknadInspektør inspektør;

    public AbstractXMLMapper(Oppslag oppslag, SøknadInspektør inspektør) {
        this.oppslag = oppslag;
        this.inspektør = inspektør;
    }

    protected SøknadType type(no.nav.vedtak.felles.xml.soeknad.v1.Soeknad søknad) {
        SøknadType type = inspektør.type(søknad);
        LOG.info("Dette er en søknad av type {} for versjon V1", type);
        return type;
    }

    protected SøknadType type(no.nav.vedtak.felles.xml.soeknad.v2.Soeknad søknad) {
        SøknadType type = inspektør.type(søknad);
        LOG.info("Dette er en søknad av type {} for versjon V2", type);
        return type;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon = " + versjon() + ", oppslag=" + oppslag + ", inspektør="
                + inspektør + "]";
    }

}
