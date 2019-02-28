package no.nav.foreldrepenger.mottak.util.jaxb;

import no.seres.xsd.nav.inntektsmelding_m._20181211.InntektsmeldingM;

public final class VedtakV2JAXBUtil extends AbstractJAXBUtil {

    public VedtakV2JAXBUtil() {
        this(false, false);
    }

    public VedtakV2JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(InntektsmeldingM.class),
                validateMarshalling, validateUnmarshalling,
                "/behandlingsprosess-vedtak-v2/xsd/vedtak-v2.xsd");
    }
}
