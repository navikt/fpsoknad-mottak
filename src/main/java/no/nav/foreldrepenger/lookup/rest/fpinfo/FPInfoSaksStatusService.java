package no.nav.foreldrepenger.lookup.rest.fpinfo;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoFagsakStatus.AVSLU;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private final URI baseURI;
    private final RestTemplate template;

    public FPInfoSaksStatusService(@Value("${fpinfo.baseuri:http://fpinfo/fpinfo/api/dokumentforsendelse}") URI baseURI,
            RestTemplate template) {
        this.baseURI = baseURI;
        this.template = template;
    }

    @Override
    public List<FPInfoSakStatus> hentSaker(AktorId id) {
        return hentSaker(id.getAktør());
    }

    @Override
    public List<FPInfoSakStatus> hentSaker(String id) {
        URI uri = uri("sak", httpHeaders("aktorId", id));
        try {
            LOG.info("Henter ikke-avsluttede saker fra {}", uri);
            return Arrays.stream(template.getForObject(uri, FPInfoSakStatus[].class))
                    .filter(s -> !s.getFagsakStatus().equals(AVSLU))
                    .collect(toList());
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente saker fra {}", uri, e);
            return Collections.emptyList();
        }
    }

    private URI uri(String pathSegment, HttpHeaders queryParams) {
        return UriComponentsBuilder.fromUri(baseURI)
                .pathSegment(pathSegment)
                .queryParams(queryParams)
                .build()
                .toUri();
    }

    private static HttpHeaders httpHeaders(String key, String value) {
        HttpHeaders params = new HttpHeaders();
        params.add(key, value);
        return params;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseURI=" + baseURI + ", template=" + template + "]";
    }

}
