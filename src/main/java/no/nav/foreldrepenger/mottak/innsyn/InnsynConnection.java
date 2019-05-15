package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.AKTOR_ID;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.SAK;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.SAKSNUMMER;
import static no.nav.foreldrepenger.mottak.innsyn.InnsynConfig.UTTAKSPLAN;
import static no.nav.foreldrepenger.mottak.innsyn.uttaksplan.ArbeidsgiverType.ORGANISASJON;
import static no.nav.foreldrepenger.mottak.innsyn.uttaksplan.ArbeidsgiverType.PRIVAT;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.URIUtil.queryParams;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SøknadDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.VedtakDTO;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.ArbeidsgiverInfo;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.SøknadsGrunnlag;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.UttaksPeriode;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.Uttaksplan;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.dto.UttaksPeriodeDTO;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.dto.UttaksplanDTO;
import no.nav.foreldrepenger.mottak.oppslag.OppslagConnection;

@Component
public class InnsynConnection extends AbstractRestConnection implements PingEndpointAware {
    private static final Logger LOG = LoggerFactory.getLogger(InnsynConnection.class);
    private final InnsynConfig config;
    private final OppslagConnection oppslag;

    public InnsynConnection(RestOperations restOperations, InnsynConfig config, OppslagConnection oppslag) {
        super(restOperations);
        this.config = config;
        this.oppslag = oppslag;
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return uri(config.getUri(), config.getPingPath());
    }

    public List<SakDTO> hentSaker(String aktørId) {
        LOG.trace("Henter saker for {}", aktørId);
        return Optional.ofNullable(
                getForObject(uri(config.getUri(), SAK, queryParams(AKTOR_ID, aktørId)), SakDTO[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    public Uttaksplan hentUttaksplan(String saksnummer) {
        LOG.trace("Henter uttaksplan for sak {}", saksnummer);
        return Optional.ofNullable(getForObject(uri(config.getUri(), UTTAKSPLAN, queryParams(SAKSNUMMER, saksnummer)),
                UttaksplanDTO.class))
                .map(this::map)
                .orElse(null);
    }

    public BehandlingDTO hentBehandling(Lenke behandlingsLenke) {
        return hent(behandlingsLenke, BehandlingDTO.class);
    }

    public VedtakDTO hentVedtak(Lenke vedtaksLenke) {
        return hent(vedtaksLenke, VedtakDTO.class);
    }

    public SøknadDTO hentSøknad(Lenke søknadsLenke) {
        return hent(søknadsLenke, SøknadDTO.class);
    }

    private <T> T hent(Lenke lenke, Class<T> clazz) {
        return Optional.ofNullable(lenke)
                .map(Lenke::getHref)
                .filter(Objects::nonNull)
                .map(l -> getForObject(URI.create(config.getUri() + l), clazz))
                .orElse(null);
    }

    private Uttaksplan map(UttaksplanDTO dto) {
        SøknadsGrunnlag grunnlag = new SøknadsGrunnlag(dto.getFamilieHendelseType(), dto.getFamilieHendelseDato(),
                dto.getDekningsgrad(), dto.getAntallBarn(), dto.getSøkerErFarEllerMedmor(), dto.getMorErAleneOmOmsorg(),
                dto.getMorHarRett(), dto.getMorErUfør(), dto.getFarMedmorErAleneOmOmsorg(), dto.getFarMedmorHarRett(),
                dto.getAnnenForelderErInformert());
        return new Uttaksplan(grunnlag, map(dto.getUttaksPerioder()));
    }

    private List<UttaksPeriode> map(List<UttaksPeriodeDTO> perioder) {
        return safeStream(perioder)
                .map(this::map)
                .collect(toList());
    }

    private UttaksPeriode map(UttaksPeriodeDTO periode) {
        return Optional.ofNullable(periode)
                .map(p -> new UttaksPeriode(p.getOppholdAarsak(), p.getOverfoeringAarsak(),
                        p.getGraderingAvslagAarsak(),
                        p.getUtsettelsePeriodeType(), p.getPeriodeResultatType(),
                        p.getGraderingInnvilget(),
                        p.getSamtidigUttak(),
                        p.getFom(), p.getTom(), p.getStønadskontotype(), p.getTrekkDager(),
                        p.getArbeidstidProsent(),
                        p.getArbeidstidProsent(), p.getGjelderAnnenPart(), p.getManueltBehandlet(),
                        p.getSamtidigUttaksprosent(),
                        p.getFlerbarnsdager(), p.getUttakArbeidType(),
                        map(periode.getArbeidsgiverAktoerId(), periode.getArbeidsgiverOrgnr())))
                .orElse(null);
    }

    private ArbeidsgiverInfo map(AktørId aktørId, String orgnr) {
        return Optional.ofNullable(orgnr)
                .map(o -> new ArbeidsgiverInfo(o, ORGANISASJON, oppslag.organisasjonsNavn(orgnr)))
                .orElse(new ArbeidsgiverInfo(Optional.ofNullable(aktørId).map(AktørId::getId).orElse(null), PRIVAT,
                        null));
    }

    @Override
    public String name() {
        return "fpinfo";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }
}
