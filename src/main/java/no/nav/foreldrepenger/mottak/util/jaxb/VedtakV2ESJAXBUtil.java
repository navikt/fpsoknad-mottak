package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.vedtak.felles.xml.vedtak.beregningsgrunnlag.es.v2.BeregningsgrunnlagEngangsstoenad;
import no.nav.vedtak.felles.xml.vedtak.personopplysninger.es.v2.PersonopplysningerEngangsstoenad;
import no.nav.vedtak.felles.xml.vedtak.v2.Vedtak;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.es.v2.VilkaarsgrunnlagAdopsjon;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.es.v2.VilkaarsgrunnlagFoedsel;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.es.v2.VilkaarsgrunnlagMedlemskap;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.es.v2.VilkaarsgrunnlagSoekersopplysningsplikt;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.es.v2.VilkaarsgrunnlagSoeknadsfrist;
import no.nav.vedtak.felles.xml.vedtak.ytelse.es.v2.YtelseEngangsstoenad;

public final class VedtakV2ESJAXBUtil extends AbstractJAXBUtil {

    public VedtakV2ESJAXBUtil() {
        this(false, false);
    }

    public VedtakV2ESJAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {

        super(contextFra(VilkaarsgrunnlagFoedsel.class,
                VilkaarsgrunnlagAdopsjon.class,
                VilkaarsgrunnlagMedlemskap.class,
                VilkaarsgrunnlagSoeknadsfrist.class,
                VilkaarsgrunnlagSoekersopplysningsplikt.class,
                BeregningsgrunnlagEngangsstoenad.class,
                YtelseEngangsstoenad.class,
                Vedtak.class,
                PersonopplysningerEngangsstoenad.class,
                no.nav.vedtak.felles.xml.vedtak.beregningsgrunnlag.es.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.oppdrag.dvh.es.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.personopplysninger.es.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.personopplysninger.dvh.es.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.ytelse.es.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.v2.ObjectFactory.class),
                validateMarshalling, validateUnmarshalling,
                "/behandlingsprosess-vedtak-v2/xsd/ytelse/ytelse-es-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/vilkaarsgrunnlag/vilkaarsgrunnlag-es-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/personopplysninger/personopplysninger-es-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/personopplysninger/personopplysninger-dvh-es-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/oppdrag/oppdrag-dvh-es-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/beregningsgrunnlag/beregningsgrunnlag-es-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/vedtak-v2.xsd");
    }
}
