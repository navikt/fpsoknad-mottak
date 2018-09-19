package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.FagsakStatus.AVSLU;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.http.errorhandling.NotFoundException;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.XMLToDomainMapper;

@Service
public class FPInfoInnsynTjeneste implements InnsynTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(FPInfoInnsynTjeneste.class);

    private final XMLToDomainMapper mapper;
    private final FPInfoConfig config;
    private final RestTemplate template;

    public FPInfoInnsynTjeneste(FPInfoConfig config, RestTemplate template,
            XMLToDomainMapper mapper) {
        this.config = config;
        this.template = template;
        this.mapper = mapper;
    }

    @Override
    public Søknad hentSøknad(String behandlingId) {
        Optional<SøknadWrapper> wrapper = get(uriFra(config.getBasePath() + "soknad",
                headers("behandlingId", behandlingId)),
                SøknadWrapper.class,
                "søknad");
        if (wrapper.isPresent()) {
            Søknad søknad = mapper.tilSøknad(wrapper.get().getXml());
            LOG.info("Fant søknad {}", søknad);
            return søknad;
        }
        throw new NotFoundException("Fant ikke søknad for " + behandlingId);
    }

    @Override
    public List<SakStatus> hentSaker(AktorId aktørId) {
        return hentSaker(aktørId.getId());
    }

    @Override
    public List<SakStatus> hentSaker(String aktørId) {
        Optional<SakStatusWrapper[]> saker = get(uriFra(config.getBasePath() + "sak", headers("aktorId", aktørId)),
                SakStatusWrapper[].class, "FPInfoSakStatusWrapper[]");

        if (!saker.isPresent()) {
            return emptyList();
        }
        List<SakStatus> fagSaker = Arrays.stream(saker.get())
                .filter(s -> !s.getFagsakStatus().equals(AVSLU))
                .map(s -> new SakStatus(s.getSaksnummer(), s.getFagsakStatus(), s.getBehandlingTema(),
                        s.getAktørId(),
                        s.getAktørIdAnnenPart(), s.getAktørIdBarn(),
                        behandlingerFra(s)))
                .collect(toList());
        LOG.info("Hentet fagsaker {}", fagSaker);
        return fagSaker;
    }

    @Override
    public Behandling hentBehandling(String behandlingId) {
        URI uri = uriFra(config.getBasePath() + "behandling", headers("behandlingId", behandlingId));
        Optional<Behandling> behandling = get(uri, Behandling.class, "behandling");
        return behandling.isPresent() ? withID(behandling.get(), uri) : null;
    }

    private List<Behandling> behandlingerFra(SakStatusWrapper sak) {
        return sak.getLenker().stream().map(s -> behandlingFra(sak.getSaksnummer(), s)).collect(toList());
    }

    private Behandling behandlingFra(String saksnr, BehandlingsLenke behandlingsLink) {
        return hentBehandling(URI.create(config.getUrl() + behandlingsLink.getHref()));
    }

    private Behandling hentBehandling(URI uri) {
        Optional<Behandling> behandling = get(uri, Behandling.class, "behandling");
        return behandling.isPresent() ? withID(behandling.get(), uri) : null;
    }

    private static Behandling withID(Behandling behandling, URI uri) {
        behandling.setId(fromUri(uri).build().getQueryParams().getFirst("behandlingId"));
        LOG.info("behandling er {}", behandling);
        return behandling;
    }

    private static HttpHeaders headers(String key, String value) {
        HttpHeaders params = new HttpHeaders();
        params.add(key, value);
        return params;
    }

    private <T> Optional<T> get(URI uri, Class<T> clazz, String type) {
        try {
            LOG.info("Henter {} fra {}", type, uri);
            T respons = template.getForObject(uri, clazz);
            LOG.trace("Fikk objekt {}", respons);
            return Optional.of(respons);
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente {} fra {}", clazz.getClass().getSimpleName(), uri, e);
            return Optional.empty();
        }
    }

    private URI uriFra(String pathSegment, HttpHeaders queryParams) {
        return fromUri(config.getUrl())
                .pathSegment(pathSegment)
                .queryParams(queryParams)
                .build()
                .toUri();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", config=" + config + ", template=" + template
                + "]";
    }

}
