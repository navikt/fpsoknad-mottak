package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.vedtak.felles.xml.vedtak.beregningsgrunnlag.fp.v2.BeregningsgrunnlagForeldrepenger;
import no.nav.vedtak.felles.xml.vedtak.personopplysninger.fp.v2.PersonopplysningerForeldrepenger;
import no.nav.vedtak.felles.xml.vedtak.uttak.fp.v2.UttakForeldrepenger;
import no.nav.vedtak.felles.xml.vedtak.v2.Vedtak;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.fp.v2.VilkaarsgrunnlagAdopsjon;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.fp.v2.VilkaarsgrunnlagFoedsel;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.fp.v2.VilkaarsgrunnlagMedlemskap;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.fp.v2.VilkaarsgrunnlagOpptjening;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.fp.v2.VilkaarsgrunnlagSoekersopplysningsplikt;
import no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.fp.v2.VilkaarsgrunnlagSoeknadsfrist;
import no.nav.vedtak.felles.xml.vedtak.ytelse.fp.v2.YtelseForeldrepenger;

public final class VedtakV2FPJAXBUtil extends AbstractJAXBUtil {

    public VedtakV2FPJAXBUtil() {
        this(false, false);
    }

    public VedtakV2FPJAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {

        super(contextFra(
                Vedtak.class,
                VilkaarsgrunnlagFoedsel.class,
                VilkaarsgrunnlagAdopsjon.class,
                VilkaarsgrunnlagMedlemskap.class,
                VilkaarsgrunnlagOpptjening.class,
                VilkaarsgrunnlagSoeknadsfrist.class,
                VilkaarsgrunnlagSoekersopplysningsplikt.class,
                BeregningsgrunnlagForeldrepenger.class,
                YtelseForeldrepenger.class,
                UttakForeldrepenger.class,
                PersonopplysningerForeldrepenger.class,
                no.nav.vedtak.felles.xml.vedtak.beregningsgrunnlag.fp.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.oppdrag.dvh.fp.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.personopplysninger.fp.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.personopplysninger.dvh.fp.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.uttak.fp.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.ytelse.fp.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.vedtak.vilkaarsgrunnlag.v2.ObjectFactory.class),
                validateMarshalling, validateUnmarshalling,
                "/behandlingsprosess-vedtak-v2/xsd/ytelse/ytelse-fp-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/vilkaarsgrunnlag/vilkaarsgrunnlag-fp-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/uttak/uttak-fp-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/personopplysninger/personopplysninger-fp-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/personopplysninger/personopplysninger-dvh-fp-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/oppdrag/oppdrag-dvh-fp-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/beregningsgrunnlag/beregningsgrunnlag-fp-v2.xsd",
                "/behandlingsprosess-vedtak-v2/xsd/vedtak-v2.xsd");
    }
}
