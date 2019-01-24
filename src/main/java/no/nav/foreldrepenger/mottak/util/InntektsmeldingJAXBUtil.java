package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.Versjon.V6;

import no.seres.xsd.nav.inntektsmelding_m._20180924.InntektsmeldingM;

public final class InntektsmeldingJAXBUtil extends AbstractJAXBUtil {

    private static final Versjon VERSJON = V6;

    public InntektsmeldingJAXBUtil() {
        super(contextFra(InntektsmeldingM.class),
                VERSJON,
                "/inntektsmelding-v6.xsd",
                "/inntektsmelding-kodeliste-v6.xsd");
    }
}
