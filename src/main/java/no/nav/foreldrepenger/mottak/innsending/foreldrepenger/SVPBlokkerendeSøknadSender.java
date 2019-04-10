package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ETTERSENDING_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Component
@Profile(PREPROD)
@ConditionalOnProperty(name = "svangerskapspenger.enabled", havingValue = "false")
public class SVPBlokkerendeSøknadSender extends FPFordelSøknadSender {

    private static final Logger LOG = LoggerFactory.getLogger(SVPBlokkerendeSøknadSender.class);

    public SVPBlokkerendeSøknadSender(FPFordelConnection connection, FPFordelKonvoluttGenerator generator) {
        super(connection, generator);
    }

    @Override
    public boolean skalSende(SøknadEgenskap egenskap) {
        LOG.trace("Sjekker om {} skal sendes", egenskap);
        boolean status = !ETTERSENDING_SVANGERSKAPSPENGER.equals(egenskap.getType());
        LOG.trace("Status {}", status);
        return status;
    }
}
