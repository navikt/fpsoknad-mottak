package no.nav.foreldrepenger.mottak.innsending.engangsstønad;

import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.innsyn.AbstractXMLMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.DefaultSøknadInspektør;
import no.nav.foreldrepenger.mottak.util.JAXBESV1Helper;
import no.nav.foreldrepenger.mottak.util.SøknadInspektør;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.OpplysningerOmBarn;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.TilknytningNorge;

//@Component
public class DokmotV1XMLMapper extends AbstractXMLMapper {

    private static final JAXBESV1Helper JAXB = new JAXBESV1Helper();

    private static final Logger LOG = LoggerFactory.getLogger(DokmotV1XMLMapper.class);

    public DokmotV1XMLMapper(Oppslag oppslag) {
        this(oppslag, new DefaultSøknadInspektør());
    }

    @Inject
    public DokmotV1XMLMapper(Oppslag oppslag, SøknadInspektør inspektør) {
        super(oppslag, inspektør);
    }

    @Override
    public Versjon versjon() {
        return V1;
    }

    public Søknad tilSøknad(String xml) {
        if (xml == null) {
            LOG.debug("Ingen søknad ble funnet");
            return null;
        }
        try {
            SoeknadsskjemaEngangsstoenad søknad = JAXB.unmarshal(xml, SoeknadsskjemaEngangsstoenad.class);
            søknad.getBruker();
            søknad.getOpplysningerOmBarn();
            søknad.getOpplysningerOmFar();
            søknad.getOpplysningerOmMor();
            søknad.getRettigheter();
            søknad.getSoknadsvalg();
            søknad.getTilknytningNorge();
            søknad.getTilleggsopplysninger();
            søknad.getVedleggListe();
            Engangsstønad ytelse = new Engangsstønad(medlemsskapFra(søknad.getTilknytningNorge()),
                    relasjonFra(søknad.getOpplysningerOmBarn()));
            /*
             * return new SoeknadsskjemaEngangsstoenad() .withBruker(brukerFra(søker.fnr))
             * .withOpplysningerOmBarn(barnFra(søknad, ytelse))
             * .withSoknadsvalg(søknadsvalgFra(søknad, ytelse))
             * .withTilknytningNorge(tilknytningFra(ytelse.getMedlemsskap(),
             * ytelse.getRelasjonTilBarn() instanceof FremtidigFødsel))
             * .withOpplysningerOmFar(farFra(ytelse.getAnnenForelder()))
             * .withTilleggsopplysninger(søknad.getTilleggsopplysninger())
             * .withVedleggListe(vedleggFra(søknad.getPåkrevdeVedlegg(),
             * søknad.getFrivilligeVedlegg()));
             */
            Søknad engangssøknad = new Søknad(LocalDateTime.now(), null, ytelse);
            engangssøknad.setTilleggsopplysninger(søknad.getTilleggsopplysninger());
            return engangssøknad;

        } catch (Exception e) {
            LOG.debug("Feil ved unmarshalling av søknad, ikke kritisk foreløpig, vi bruker ikke dette til noe", e);
            return null;
        }
    }

    private RelasjonTilBarn relasjonFra(OpplysningerOmBarn opplysningerOmBarn) {
        opplysningerOmBarn.getAntallBarn();
        return null;
    }

    private Medlemsskap medlemsskapFra(TilknytningNorge tilknytningNorge) {
        // TODO Auto-generated method stub
        return null;
    }

}
