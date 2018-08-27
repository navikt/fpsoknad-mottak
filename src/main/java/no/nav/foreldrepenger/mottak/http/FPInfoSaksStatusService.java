package no.nav.foreldrepenger.mottak.http;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoFagsakStatus.AVSLU;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.BehandlingsStatus;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoFagsakYtelseType;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoSakStatus;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.SaksStatusService;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.Vedtak;

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
    public List<FPInfoSakStatus> hentSaker(AktorId id, FPInfoFagsakYtelseType... typer) {
        URI uri = uri("sak", httpHeaders("aktorId", id.getId()));
        try {
            LOG.info("Henter ikke-avsluttede saker med type{} {} fra {}", typer.length == 0 ? "" : "r",
                    Arrays.stream(typer).map(s -> s.name()).collect(joining(",")), uri);
            return Arrays.stream(template.getForObject(uri, FPInfoSakStatus[].class))
                    .filter(s -> s.getFagsakYtelseType().equals(FPInfoFagsakYtelseType.FP))
                    .filter(s -> !s.getFagsakStatus().equals(AVSLU))
                    .collect(toList());
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente saker fra {}", uri, e);
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
