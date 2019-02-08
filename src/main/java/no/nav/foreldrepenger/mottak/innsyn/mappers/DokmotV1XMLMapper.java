package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.jaxb.ESV1JAXBUtil;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.OpplysningerOmBarn;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.TilknytningNorge;

@Component
public class DokmotV1XMLMapper implements XMLMapper {

    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V1, INITIELL_ENGANGSSTØNAD);

    private static final ESV1JAXBUtil JAXB = new ESV1JAXBUtil();

    private static final Logger LOG = LoggerFactory.getLogger(DokmotV1XMLMapper.class);

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        // TODO incomplete
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
            Søknad engangssøknad = new Søknad(LocalDateTime.now(), null, ytelse);
            engangssøknad.setTilleggsopplysninger(søknad.getTilleggsopplysninger());
            return engangssøknad;

        } catch (Exception e) {
            LOG.debug("Feil ved unmarshalling av søknad, ikke kritisk foreløpig, vi bruker ikke dette til noe", e);
            return null;
        }
    }

    private static RelasjonTilBarn relasjonFra(OpplysningerOmBarn opplysningerOmBarn) {
        opplysningerOmBarn.getAntallBarn();
        return null;
    }

    private static Medlemsskap medlemsskapFra(TilknytningNorge tilknytningNorge) {
        // TODO Auto-generated method stub
        return null;
    }

}
