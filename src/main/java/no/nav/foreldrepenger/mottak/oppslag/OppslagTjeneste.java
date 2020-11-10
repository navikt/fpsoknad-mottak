package no.nav.foreldrepenger.mottak.oppslag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Service
@ConditionalOnProperty(name = "oppslag.stub", havingValue = "false", matchIfMissing = true)
public class OppslagTjeneste implements Oppslag {
    private static final Logger LOG = LoggerFactory.getLogger(OppslagTjeneste.class);
    private final TPSConnection tpsConn;
    private final PDLConnection pdlConn;
    private final TokenUtil tokenHelper;

    public OppslagTjeneste(PDLConnection pdlConn, TPSConnection tpsConn, TokenUtil tokenHelper) {
        this.tpsConn = tpsConn;
        this.pdlConn = pdlConn;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public String ping() {
        return pdlConn.ping();
    }

    @Override
    public Person person() {
        return /* sammenlign(tpsConn.søker(), */pdlPerson(); // );
    }

    @Override
    public AktørId aktørId() {
        return aktørId(tokenHelper.fnr());
    }

    @Override
    public AktørId aktørId(Fødselsnummer fnr) {
        return pdlAktørId(fnr);
    }

    @Override
    public Fødselsnummer fnr(AktørId aktørId) {
        return pdlFnr(aktørId);
    }

    @Override
    public Navn navn(String id) {
        return pdlNavn(id);
    }

    private Fødselsnummer pdlFnr(AktørId aktørId) {
        try {
            return pdlConn.fødselsnummerFor(aktørId);
        } catch (Exception e) {
            LOG.warn("Feil ved oppslag PDL fnr");
            return null;
        }
    }

    private Person pdlPerson() {
        try {
            var p = pdlConn.hentSøker();
            var np = new Person(p.getId(), p.getNavn(), p.getFødselsdato(), p.getMålform(), p.getLandKode(),
                    p.getBankkonto());
            np.setAktørId(p.getAktørId());
            return np;
        } catch (Exception e) {
            LOG.warn("Feil ved oppslag PDL person", e);
            return null;
        }
    }

    private AktørId pdlAktørId(Fødselsnummer fnr) {
        try {
            return pdlConn.aktøridFor(fnr);
        } catch (Exception e) {
            LOG.warn("Feil ved oppslag PDL aktør", e);
            return null;
        }
    }

    private Navn pdlNavn(String id) {
        try {
            return pdlConn.navnFor(id);
        } catch (Exception e) {
            LOG.warn("Feil ved oppslag PDL navn", e);
            return null;
        }
    }

    private <T> T sammenlign(T tps, T pdl) {
        try {
            String name = tps.getClass().getSimpleName();
            LOG.info("Sammenligner {}, PDL bruk er {}", name, tpsConn.isBrukPdl());
            if (!tps.equals(pdl)) {
                LOG.warn("TPS-{} og PDL-{} er ulike, tps={}, pdl={}", name, name, tps, pdl);
                return tpsConn.isBrukPdl() ? pdl : tps;
            } else {

                LOG.info("TPS-{} og PDL-{} er like", name, name);
                return pdl;
            }
        } catch (Exception e) {
            LOG.warn("Feil ved sammenligning", e);
            return tps;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [tpsConn=" + tpsConn + ", pdlConn=" + pdlConn + ", tokenHelper=" + tokenHelper + "]";
    }
}
