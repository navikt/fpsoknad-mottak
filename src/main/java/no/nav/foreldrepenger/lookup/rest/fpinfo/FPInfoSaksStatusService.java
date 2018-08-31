package no.nav.foreldrepenger.lookup.rest.fpinfo;

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;

@Service
public class FPInfoSaksStatusService implements SaksStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(FPInfoSaksStatusService.class);
    private static final String PATH = "fpinfo/api/dokumentforsendelse/";

    private final URI baseURI;
    private final RestTemplate template;

    public FPInfoSaksStatusService(@Value("${fpinfo.baseuri:http://fpinfo}") URI baseURI,
            RestTemplate template) {
        this.baseURI = baseURI;
        this.template = template;
    }

    @Override
    public List<FPInfoSakStatus> hentSaker(AktorId aktørId) {
        return hentSaker(aktørId.getAktør());
    }

    @Override
    public List<FPInfoSakStatus> hentSaker(String aktørId) {
        Optional<FPInfoSakStatus[]> saker = hentObjekt(uri(PATH + "sak", headers("aktorId", aktørId)),
                FPInfoSakStatus[].class, "FPInfoSakStatus[]");

        if (!saker.isPresent()) {
            return Collections.emptyList();
        }
        List<FPInfoSakStatus> filtrerteSaker = Arrays.stream(saker.get())
                .filter(s -> !s.getFagsakStatus().equals(FPInfoFagsakStatus.AVSLU))
                .collect(toList());
        filtrerteSaker.stream().forEach(this::behandlinger);
        return filtrerteSaker;
    }

    @Override
    public Behandling hentBehandling(String behandlingId) {
        return hentBehandling(uri(PATH + "behandling", headers("behandlingId", behandlingId)));
    }

    private Behandling hentBehandling(URI uri) {
        Optional<Behandling> behandling = hentObjekt(uri, Behandling.class, "behandling");
        return behandling.isPresent() ? behandling.get() : null;
    }

    private List<Behandling> behandlinger(FPInfoSakStatus sak) {
        return sak.getLenker().stream().map(s -> hentBehandling(sak.getSaksnummer(), s)).collect(Collectors.toList());
    }

    private Behandling hentBehandling(String saksnr, BehandlingsLenke behandlingsLink) {
        return hentBehandling(URI.create(baseURI + behandlingsLink.getHref()));
    }

    private <T> Optional<T> hentObjekt(URI uri, Class<T> clazz, String type) {
        try {
            LOG.info("Henter {} fra {}", type, uri);
            T respons = template.getForObject(uri, clazz);
            LOG.info("Fikk objekt {}", respons);
            return Optional.of(respons);
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente {} fra {}", clazz.getClass().getSimpleName(), uri, e);
            return Optional.empty();
        }
    }

    private URI uri(String pathSegment, HttpHeaders queryParams) {
        return UriComponentsBuilder.fromUri(baseURI)
                .pathSegment(pathSegment)
                .queryParams(queryParams)
                .build()
                .toUri();
    }

    private static HttpHeaders headers(String key, String value) {
        HttpHeaders params = new HttpHeaders();
        params.add(key, value);
        return params;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseURI=" + baseURI + ", template=" + template + "]";
    }

}
