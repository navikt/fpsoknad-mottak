package no.nav.foreldrepenger.mottak.http;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.XMLTilSøknadMapper;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.Behandling;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConnection;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.Lenke;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.Sak;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.SakStatusWrapper;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.SøknadWrapper;

@Service
public class InnsynTjeneste implements Innsyn {

    private static final Logger LOG = LoggerFactory.getLogger(InnsynTjeneste.class);

    private final XMLTilSøknadMapper mapper;
    private final InnsynConnection connection;

    public InnsynTjeneste(InnsynConnection connection, XMLTilSøknadMapper mapper) {
        this.connection = connection;
        this.mapper = mapper;
    }

    @Override
    public Søknad hentSøknad(String behandlingId) {
        SøknadWrapper søknad = connection.hentSøknad(behandlingId);
        return søknad != null ? mapper.tilSøknad(søknad.getXml()) : null;
    }

    @Override
    public List<Sak> hentSaker(AktorId aktørId) {
        return hentSaker(aktørId.getId());
    }

    @Override
    public List<Sak> hentSaker(String aktørId) {
        LOG.info("Henter saker for {}", aktørId);
        List<Sak> saker = safeStream(connection.hentSaker(aktørId))
                .map(s -> new Sak(s.getSaksnummer(), s.getFagsakStatus(), s.getBehandlingTema(),
                        s.getAktørId(), s.getAktørIdAnnenPart(), s.getAktørIdBarn(),
                        hentBehandling(s)))
                .collect(toList());
        LOG.info("Hentet saker {}", saker);
        return saker;
    }

    @Override
    public Behandling hentBehandling(String behandlingId) {
        return connection.hentBehandling(behandlingId);
    }

    private List<Behandling> hentBehandling(SakStatusWrapper sak) {
        return sak.getBehandlingsLenker().stream().map(s -> hentBehandling(sak.getSaksnummer(), s)).collect(toList());
    }

    private Behandling hentBehandling(String saksnr, Lenke lenke) {
        return connection.hentBehandling(lenke);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", connection=" + connection + "]";
    }

}
