package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.HttpMethod.GET;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.BehandlingsStatus;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoSakStatus;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoSaksStatusService;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.Vedtak;

@Service
public class SaksStatusService implements FPInfoSaksStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(SaksStatusService.class);

    private final URI baseURI;
    private final RestTemplate template;

    public SaksStatusService(@Value("${fpinfo.baseuri:http://fpinfo/fpinfo/api/dokumentforsendelse}") URI baseURI,
            RestTemplate template) {
        this.baseURI = baseURI;
        this.template = template;
    }

    @Override
    public List<FPInfoSakStatus> hentSaker(AktorId id) {
        return queryForList("/sak", httpHeaders("aktorId", id.getId()), FPInfoSakStatus.class);
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

    private <T> List<T> queryForList(String pathSegment, HttpHeaders params, Class<T> clazz) {
        URI uri = uri(pathSegment, params);
        try {
            LOG.info("Henter fra {}", uri);
            return template.exchange(uri, GET, null, new ParameterizedTypeReference<List<T>>() {
            }).getBody();
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente liste fra {}", uri);
            return Collections.emptyList();
        }
    }

    private URI uri(String pathSegment, HttpHeaders params) {
        return UriComponentsBuilder.fromUri(baseURI)
                .pathSegment(pathSegment)
                .queryParams(params)
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
