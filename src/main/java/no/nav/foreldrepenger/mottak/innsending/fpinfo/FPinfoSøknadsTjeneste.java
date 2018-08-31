package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.domain.Søknad;

@Service
public class FPinfoSøknadsTjeneste implements SøknadsTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(FPinfoSøknadsTjeneste.class);

    private static final String PATH = "fpinfo/api/dokumentforsendelse/";

    private final URI baseURI;
    private final RestTemplate template;

    public FPinfoSøknadsTjeneste(@Value("${fpinfo.baseuri:http://fpinfo}") URI baseURI, RestTemplate template) {
        this.baseURI = baseURI;
        this.template = template;
    }

    @Override
    public Søknad hentSøknad(String behandlingId) {
        Optional<SøknadWrapper> wrapper = hentObjekt(uri(PATH + "soknad", headers("behandlingId", behandlingId)),
                SøknadWrapper.class,
                "søknad");
        return null; // TODO
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
}
