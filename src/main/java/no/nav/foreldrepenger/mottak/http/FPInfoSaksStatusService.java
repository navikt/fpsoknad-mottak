package no.nav.foreldrepenger.mottak.http;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.BehandlingsStatus;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoSakStatus;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.SaksStatusService;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.Vedtak;

@Service
public class FPInfoSaksStatusService implements SaksStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(FPInfoSaksStatusService.class);

    @Inject
    private ObjectMapper mapper;

    private final URI baseURI;
    private final RestTemplate template;

    public FPInfoSaksStatusService(@Value("${fpinfo.baseuri:http://fpinfo/fpinfo/api/dokumentforsendelse}") URI baseURI,
            RestTemplate template) {
        this.baseURI = baseURI;
        this.template = template;
    }

    @Override
    public List<FPInfoSakStatus> hentSaker(AktorId id) {
        URI uri = uri("sak", httpHeaders("aktorId", id.getId()));
        try {
            LOG.info("Henter fra {}", uri);
            ResponseEntity<String> respons = template.exchange(uri, HttpMethod.GET, null, String.class);
            String body = respons.getBody();
            return Arrays.asList(mapper.readValue(body, FPInfoSakStatus[].class));
            // return Arrays.asList(template.getForObject(uri, FPInfoSakStatus[].class));
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente saker fra {}", uri);
            return Collections.emptyList();
        }
    }

    @Override
    public Vedtak hentVedtak(String behandlingsId) {
        throw new NotImplementedException("vedtak");
    }

    @Override
    public Søknad hentSøknad(String behandlingsId) {
        throw new NotImplementedException("søknad");
    }

    @Override
    public BehandlingsStatus hentBehandlingsStatus(String behandlingsId) {
        throw new NotImplementedException("behandlingsstatus");
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
