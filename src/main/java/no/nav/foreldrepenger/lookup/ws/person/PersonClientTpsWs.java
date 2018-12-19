package no.nav.foreldrepenger.lookup.ws.person;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.errorhandling.NotFoundException;
import no.nav.foreldrepenger.errorhandling.RemoteUnavailableException;
import no.nav.foreldrepenger.errorhandling.TokenExpiredException;
import no.nav.foreldrepenger.errorhandling.UnauthorizedException;
import no.nav.foreldrepenger.lookup.TokenHandler;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.lookup.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.lookup.ws.person.PersonMapper.barn;
import static no.nav.foreldrepenger.lookup.ws.person.PersonMapper.person;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.*;

public class PersonClientTpsWs implements PersonClient {

    private static final Logger LOG = LoggerFactory.getLogger(PersonClientTpsWs.class);

    private final PersonV3 person;
    private final PersonV3 healthIndicator;
    private final Barnutvelger barnutvelger;
    protected final TokenHandler tokenHandler;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.tps");

    public PersonClientTpsWs(PersonV3 person, PersonV3 healthIndicator, TokenHandler tokenHandler,
            Barnutvelger barnutvelger) {
        this.person = Objects.requireNonNull(person);
        this.healthIndicator = healthIndicator;
        this.tokenHandler = tokenHandler;
        this.barnutvelger = Objects.requireNonNull(barnutvelger);
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger TPS");
            healthIndicator.ping();
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    @Override
    @Timed("lookup.person")
    public Person hentPersonInfo(ID id) {
        try {
            LOG.info("Slår opp person");
            LOG.info(CONFIDENTIAL, "Fra ID {}", id);
            HentPersonRequest request = RequestUtils.request(id.getFnr(), KOMMUNIKASJON, BANKKONTO, FAMILIERELASJONER);
            no.nav.tjeneste.virksomhet.person.v3.informasjon.Person tpsPerson = hentPerson(request).getPerson();
            Person p = person(id, tpsPerson, barnFor(tpsPerson));
            LOG.info(CONFIDENTIAL, "Person er {}", p);
            return p;
        } catch (SOAPFaultException e) {
            ERROR_COUNTER.increment();
            if (tokenHandler.isExpired()) {
                throw new TokenExpiredException(tokenHandler.getExp(), e);
            }
            throw e;
        } catch (WebServiceException e) {
            ERROR_COUNTER.increment();
            throw new RemoteUnavailableException(e);
        }

    }

    private List<Barn> barnFor(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        PersonIdent id = (PersonIdent) person.getAktoer();
        String idType = id.getIdent().getType().getValue();
        switch (idType) {
        case RequestUtils.FNR:
        case RequestUtils.DNR:
            Fødselsnummer fnrSøker = new Fødselsnummer(id.getIdent().getIdent());
            return person.getHarFraRolleI().stream()
                    .filter(this::isBarn)
                    .map(s -> hentBarn(s, fnrSøker))
                    .filter(Objects::nonNull)
                    .filter(barn -> barnutvelger.erStonadsberettigetBarn(fnrSøker, barn))
                    .collect(toList());
        default:
            throw new IllegalStateException("ID type " + idType + " ikke støttet");
        }
    }

    private boolean isBarn(Familierelasjon rel) {
        return rel.getTilRolle().getValue().equals(RequestUtils.BARN);
    }

    private boolean isForelder(Familierelasjon rel) {
        String rolle = rel.getTilRolle().getValue();
        return rolle.equals(RequestUtils.MOR) || rolle.equals(RequestUtils.FAR);
    }

    private Barn hentBarn(Familierelasjon rel, Fødselsnummer fnrSøker) {
        NorskIdent id = ((PersonIdent) rel.getTilPerson().getAktoer()).getIdent();
        if (RequestUtils.isFnr(id)) {
            Fødselsnummer fnrBarn = new Fødselsnummer(id.getIdent());
            no.nav.tjeneste.virksomhet.person.v3.informasjon.Person tpsBarn = hentPerson(
                    RequestUtils.request(fnrBarn, FAMILIERELASJONER)).getPerson();

            AnnenForelder annenForelder = tpsBarn.getHarFraRolleI().stream()
                    .filter(this::isForelder)
                    .map(this::toFødselsnummer)
                    .filter(Objects::nonNull)
                    .filter(fnr -> !fnr.equals(fnrSøker))
                    .map(fnr -> hentPerson(RequestUtils.request(fnr)).getPerson())
                    .map(PersonMapper::annenForelder)
                    .findFirst()
                    .orElse(null);

            return barn(id, fnrSøker, tpsBarn, annenForelder);
        }
        return null;
    }

    private Fødselsnummer toFødselsnummer(Familierelasjon rel) {
        NorskIdent id = ((PersonIdent) rel.getTilPerson().getAktoer()).getIdent();
        if (RequestUtils.isFnr(id)) {
            return new Fødselsnummer(id.getIdent());
        }
        else {
            return null;
        }
    }

    private HentPersonResponse hentPerson(HentPersonRequest request) {
        try {
            HentPersonResponse p = person.hentPerson(request);
            LOG.info("Fant person OK");
            return p;
        } catch (HentPersonPersonIkkeFunnet e) {
            LOG.warn("Fant ikke person", e);
            throw new NotFoundException(e);
        } catch (HentPersonSikkerhetsbegrensning e) {
            LOG.warn("Sikkerhetsbegrensning ved oppslag.", e);
            throw new UnauthorizedException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [person=" + person + "]";
    }

}
