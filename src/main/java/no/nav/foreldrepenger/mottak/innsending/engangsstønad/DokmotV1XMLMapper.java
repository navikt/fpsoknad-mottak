package no.nav.foreldrepenger.mottak.innsending.engangsstønad;

import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.AbstractXMLMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.util.JAXBESV1Helper;
import no.nav.foreldrepenger.mottak.util.SøknadInspektør;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.OpplysningerOmBarn;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.TilknytningNorge;

@Component
public class DokmotV1XMLMapper extends AbstractXMLMapper {

    private static final JAXBESV1Helper JAXB = new JAXBESV1Helper();

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
        return Lists.newArrayList(SøknadType.ENGANGSSØKNAD);
    }

    @Override
    public Søknad tilSøknad(String xml) {
        return null;
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
