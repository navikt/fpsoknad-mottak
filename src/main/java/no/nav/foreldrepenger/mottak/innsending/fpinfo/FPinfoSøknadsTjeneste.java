package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoFagsakStatus.AVSLU;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.http.errorhandling.NotFoundException;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.ForeldrepengerSøknadMapper;

@Service
public class FPinfoSøknadsTjeneste implements SøknadsTjeneste {

    private static final String PATH = "fpinfo/api/dokumentforsendelse/";

    private static final Logger LOG = LoggerFactory.getLogger(FPinfoSøknadsTjeneste.class);

    @Inject
    private ForeldrepengerSøknadMapper generator;
    private final URI baseURI;
    private final RestTemplate template;

    public FPinfoSøknadsTjeneste(@Value("${fpinfo.baseuri:http://fpinfo}") URI baseURI, RestTemplate template) {
        this.baseURI = baseURI;
        this.template = template;
    }

    @Override
    public Søknad hentSøknad(String behandlingId) {
        Optional<SøknadWrapper> wrapper = get(uriFra(PATH + "soknad", headers("behandlingId", behandlingId)),
                SøknadWrapper.class,
                "søknad");
        if (wrapper.isPresent()) {
            Søknad søknad = generator.tilSøknad(wrapper.get().getXml());
            LOG.info("Fant søknad {}", søknad);
            return søknad;
        }
        throw new NotFoundException("Fant ikke søknad for " + behandlingId);
    }

    @Override
    public List<FPInfoSakStatus> hentSaker(AktorId aktørId) {
        return hentSaker(aktørId.getId());
    }

    @Override
    public List<FPInfoSakStatus> hentSaker(String aktørId) {
        Optional<FPInfoSakStatusWrapper[]> saker = get(uriFra(PATH + "sak", headers("aktorId", aktørId)),
                FPInfoSakStatusWrapper[].class, "FPInfoSakStatusWrapper[]");

        if (!saker.isPresent()) {
            return Collections.emptyList();
        }
        List<FPInfoSakStatus> fagSaker = Arrays.stream(saker.get())
                .filter(s -> !s.getFagsakStatus().equals(AVSLU))
                .map(s -> new FPInfoSakStatus(s.getSaksnummer(), s.getFagsakStatus(), s.getBehandlingTema(),
                        s.getAktørId(),
                        s.getAktørIdAnnenPart(), s.getAktørIdBarn(),
                        behandlingerFra(s)))
                .collect(toList());
        LOG.info("Henter fagsaker {}", fagSaker);
        return fagSaker;
    }

    @Override
    public Behandling hentBehandling(String behandlingId) {
        URI uri = uriFra(PATH + "behandling", headers("behandlingId", behandlingId));
        Optional<Behandling> behandling = get(uri, Behandling.class, "behandling");
        return behandling.isPresent() ? withID(behandling.get(), uri) : null;
    }

    private List<Behandling> behandlingerFra(FPInfoSakStatusWrapper sak) {
        return sak.getLenker().stream().map(s -> behandlingFra(sak.getSaksnummer(), s)).collect(toList());
    }

    private Behandling behandlingFra(String saksnr, BehandlingsLenke behandlingsLink) {
        return hentBehandling(URI.create(baseURI + behandlingsLink.getHref()));
    }

    private Behandling hentBehandling(URI uri) {
        Optional<Behandling> behandling = get(uri, Behandling.class, "behandling");
        return behandling.isPresent() ? withID(behandling.get(), uri) : null;
    }

    private static Behandling withID(Behandling behandling, URI uri) {
        UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("behandlingId");
        behandling.setId(UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("behandlingId"));
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
        return UriComponentsBuilder.fromUri(baseURI)
                .pathSegment(pathSegment)
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}
