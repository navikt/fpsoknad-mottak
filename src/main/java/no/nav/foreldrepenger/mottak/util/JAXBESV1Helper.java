package no.nav.foreldrepenger.mottak.util;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;

@Component
public final class JAXBESV1Helper extends AbstractJaxb {

    public JAXBESV1Helper() {
        super(contextFra(SoeknadsskjemaEngangsstoenad.class, Dokumentforsendelse.class), null);
    }

    @Override
    Versjon version() {
        return Versjon.V1;
    }
}
