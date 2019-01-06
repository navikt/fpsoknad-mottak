package no.nav.foreldrepenger.mottak.innsending.engangsstønad;

import static java.util.Collections.singletonList;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENGANGSSØKNAD;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.AbstractXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.ESV1JAXBUtil;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.OpplysningerOmBarn;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.TilknytningNorge;

@Component
public class DokmotV1XMLMapper extends AbstractXMLMapper {

    private static final ESV1JAXBUtil JAXB = new ESV1JAXBUtil();

    private static final Logger LOG = LoggerFactory.getLogger(DokmotV1XMLMapper.class);

    public DokmotV1XMLMapper(Oppslag oppslag) {
        this(oppslag, new XMLStreamSøknadInspektør());
    }

    @Inject
    public DokmotV1XMLMapper(Oppslag oppslag, SøknadInspektør inspektør) {
        super(oppslag, inspektør);
    }

    @Override
    public Versjon versjon() {
        return V1;
    }

    @Override
    public List<SøknadType> typer() {
        return singletonList(ENGANGSSØKNAD);
    }

    @Override
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
