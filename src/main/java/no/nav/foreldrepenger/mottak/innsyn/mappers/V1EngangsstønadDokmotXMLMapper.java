package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD_DOKMOT;
import static no.nav.foreldrepenger.common.util.Versjon.V1;

import java.time.LocalDate;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.util.jaxb.ESV1JAXBUtil;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.OpplysningerOmBarn;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.TilknytningNorge;

@Component
public class V1EngangsstønadDokmotXMLMapper implements XMLSøknadMapper {
    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V1, INITIELL_ENGANGSSTØNAD_DOKMOT);
    private final ESV1JAXBUtil jaxb;
    private static final Logger LOG = LoggerFactory.getLogger(V1EngangsstønadDokmotXMLMapper.class);

    @Inject
    public V1EngangsstønadDokmotXMLMapper() {
        this(true);
    }

    public V1EngangsstønadDokmotXMLMapper(boolean validate) {
        this.jaxb = new ESV1JAXBUtil(validate);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        return Optional.ofNullable(xml)
                .map(this::esDokmot)
                .orElse(null);
    }

    private Søknad esDokmot(String xml) {
        try {
            SoeknadsskjemaEngangsstoenad søknad = jaxb.unmarshal(xml, SoeknadsskjemaEngangsstoenad.class);
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
            Søknad engangssøknad = new Søknad(LocalDate.now(), null, ytelse);
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
