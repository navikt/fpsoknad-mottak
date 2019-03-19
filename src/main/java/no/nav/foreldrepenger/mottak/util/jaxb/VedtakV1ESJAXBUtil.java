package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.vedtak.felles.xml.vedtak.beregningsgrunnlag.v1.BeregningsgrunnlagEngangsstoenad;
import no.nav.vedtak.felles.xml.vedtak.personopplysninger.v1.PersonopplysningerEngangsstoenad;
import no.nav.vedtak.felles.xml.vedtak.v1.Vedtak;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.v1.VilkaarsgrunnlagAdopsjon;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.v1.VilkaarsgrunnlagFoedsel;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.v1.VilkaarsgrunnlagMedlemskap;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.v1.VilkaarsgrunnlagSoekersopplysningsplikt;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.v1.VilkaarsgrunnlagSoeknadsfrist;
import no.nav.vedtak.felles.xml.vedtak.ytelse.v1.YtelseEngangsstoenad;

public final class VedtakV1ESJAXBUtil extends AbstractJAXBUtil {

    public VedtakV1ESJAXBUtil() {
        this(false, false);
    }

    public VedtakV1ESJAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {

        super(contextFra(VilkaarsgrunnlagFoedsel.class,
                VilkaarsgrunnlagAdopsjon.class,
                VilkaarsgrunnlagMedlemskap.class,
                VilkaarsgrunnlagSoeknadsfrist.class,
                VilkaarsgrunnlagSoekersopplysningsplikt.class,
                BeregningsgrunnlagEngangsstoenad.class,
                YtelseEngangsstoenad.class,
                Vedtak.class,
                PersonopplysningerEngangsstoenad.class,
                no.nav.vedtak.felles.xml.vedtak.beregningsgrunnlag.v1.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.personopplysninger.v1.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.ytelse.v1.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.v1.ObjectFactory.class),
                validateMarshalling, validateUnmarshalling,
                "/behandlingsprosess-vedtak-v1/xsd/ytelse/ytelse-v1.xsd",
                "/behandlingsprosess-vedtak-v1/xsd/vilkaarsgrunnlag/vilkaarsgrunnlag-v1.xsd",
                "/behandlingsprosess-vedtak-v1/xsd/personopplysninger/personopplysninger-v1.xsd",
                "/behandlingsprosess-vedtak-v1/xsd/beregningsgrunnlag/beregningsgrunnlag-v1.xsd",
                "/behandlingsprosess-vedtak-v1/xsd/vedtak-v2.xsd");
    }
}
