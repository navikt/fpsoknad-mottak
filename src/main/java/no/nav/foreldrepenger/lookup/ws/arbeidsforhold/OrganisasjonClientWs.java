package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.tjeneste.virksomhet.organisasjon.v5.binding.HentOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v5.binding.HentOrganisasjonUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjon.v5.binding.OrganisasjonV5;
import no.nav.tjeneste.virksomhet.organisasjon.v5.informasjon.SammensattNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v5.informasjon.UstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v5.meldinger.HentOrganisasjonRequest;
import no.nav.tjeneste.virksomhet.organisasjon.v5.meldinger.HentOrganisasjonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.stream.Collectors.*;

public class OrganisasjonClientWs implements OrganisasjonClient {

    private static final Logger log = LoggerFactory.getLogger(OrganisasjonClientWs.class);
    private static final Counter errorCounter = Metrics.counter("errors.lookup.organisasjon");

    private OrganisasjonV5 organisasjonV5;
    private OrganisasjonV5 healthIndicator;

    @Inject
    public OrganisasjonClientWs(OrganisasjonV5 organisasjonV5, OrganisasjonV5 healthIndicator) {
        this.organisasjonV5 = organisasjonV5;
        this.healthIndicator = healthIndicator;
    }

    @Override
    public void ping() {
        try {
            log.info("Pinger Organisasjon");
            healthIndicator.ping();
        } catch (Exception ex) {
            errorCounter.increment();
            throw ex;
        }
    }

    @Override
    public Optional<String> nameFor(String orgnr) {
        try {
            HentOrganisasjonRequest request = new HentOrganisasjonRequest();
            request.setOrgnummer(orgnr);
            final HentOrganisasjonResponse response = organisasjonV5.hentOrganisasjon(request);
            return Optional.ofNullable(name(response.getOrganisasjon().getNavn()));
        } catch (HentOrganisasjonUgyldigInput ex) {
            log.warn("Invalid input error when looking for organisation " + orgnr, ex);
            errorCounter.increment();
        } catch (HentOrganisasjonOrganisasjonIkkeFunnet ex) {
            log.warn("Couldn't find organisation " + orgnr, ex);
            errorCounter.increment();
        } catch (Exception ex) {
            log.warn("Error while looking up organisation " + orgnr, ex);
            errorCounter.increment();
        }

        return Optional.empty();
    }

    private String name(SammensattNavn sammensattNavn) {
        return UstrukturertNavn.class.cast(sammensattNavn).getNavnelinje()
            .stream()
            .filter(this::isNotEmpty)
            .collect(joining(","));
    }

    private boolean isNotEmpty(String str) {
        return str != null && str.trim().length() != 0;
    }

}
