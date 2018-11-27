package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.AKTOR_ID;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.SAK;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.SAKSNUMMER;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.UTTAKSPLAN;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.Pingable;
import no.nav.foreldrepenger.mottak.util.TokenHandler;

@Component
public class InnsynConnection extends AbstractRestConnection implements Pingable {
    private static final Logger LOG = LoggerFactory.getLogger(InnsynConnection.class);

    private final InnsynConfig config;

    public InnsynConnection(RestTemplate template, TokenHandler tokenHandler, InnsynConfig config) {
        super(template, tokenHandler);
        this.config = config;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    public URI pingEndpoint() {
        return uri(config.getUri(), config.getPingPath());
    }

    public SøknadWrapper hentSøknad(Lenke søknadsLenke) {
        if (søknadsLenke != null && søknadsLenke.getHref() != null) {
            return Optional
                    .ofNullable(getForObject(URI.create(config.getUri() + søknadsLenke.getHref()), SøknadWrapper.class))
                    .orElse(null);
        }
        return null;
    }

    public List<SakWrapper> hentSaker(String aktørId) {
        LOG.trace("Henter saker");
        return Optional.ofNullable(getForObject(uri(config.getUri(), SAK,
                queryParams(AKTOR_ID, aktørId)), SakWrapper[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    public List<UttaksPeriode> hentUttaksplan(String saksnummer) {
        LOG.trace("Henter uttaksplan");
        return Optional.ofNullable(getForObject(uri(config.getUri(), UTTAKSPLAN,
                queryParams(SAKSNUMMER, saksnummer)), UttaksPeriode[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    public BehandlingWrapper hentBehandling(Lenke behandlingsLenke) {
        LOG.trace("Henter behandling");
        return Optional.ofNullable(
                getForObject(URI.create(config.getUri() + behandlingsLenke.getHref()), BehandlingWrapper.class))
                .orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }

    @Override
    public String name() {
        return "fpinfo";
    }

}
