package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.Versjon.V20180924;

import no.seres.xsd.nav.inntektsmelding_m._20180924.InntektsmeldingM;

public final class InntektsmeldingJAXBUtil extends AbstractJAXBUtil {

    private static final Versjon VERSJON = V20180924;

    public InntektsmeldingJAXBUtil() {
        this(false, false);
    }

    public InntektsmeldingJAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(InntektsmeldingM.class),
                VERSJON,
                validateMarshalling, validateUnmarshalling,
                "/inntektsmelding-v6.xsd",
                "/inntektsmelding-kodeliste-v6.xsd");
    }

}
