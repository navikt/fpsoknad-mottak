package no.nav.foreldrepenger.lookup.ws.person;

import static no.nav.foreldrepenger.lookup.ws.person.PersonMapper.barn;
import static no.nav.foreldrepenger.lookup.ws.person.PersonMapper.person;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.BANKKONTO;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.FAMILIERELASJONER;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.KOMMUNIKASJON;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.errorhandling.NotFoundException;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

public class PersonClientTpsWs implements PersonClient {

    private static final Logger LOG = LoggerFactory.getLogger(PersonClientTpsWs.class);

    private final PersonV3 person;
    private final PersonV3 healthIndicator;
    private final Barnutvelger barnutvelger;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.tps");

    public PersonClientTpsWs(PersonV3 person, PersonV3 healthIndicator, Barnutvelger barnutvelger) {
        this.person = Objects.requireNonNull(person);
        this.healthIndicator = healthIndicator;
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
    public Person hentPersonInfo(ID id) {
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Slår opp person fra {}", id);
            }
            else {
                LOG.info("Slår opp person");
            }
            HentPersonRequest request = RequestUtils.request(id.getFnr(), KOMMUNIKASJON, BANKKONTO, FAMILIERELASJONER);
            no.nav.tjeneste.virksomhet.person.v3.informasjon.Person tpsPerson = hentPerson(request).getPerson();
            Person p = person(id, tpsPerson, barnFor(tpsPerson));
            if (LOG.isTraceEnabled()) {
                LOG.trace("Fant person {}", p);
            }
            else {
                LOG.info("Fant en person OK");
            }
            return p;
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw new RuntimeException(ex);
        }

    }

    private List<Barn> barnFor(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        PersonIdent id = PersonIdent.class.cast(person.getAktoer());
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
                    .collect(Collectors.toList());
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
        NorskIdent id = PersonIdent.class.cast(rel.getTilPerson().getAktoer()).getIdent();
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
        NorskIdent id = PersonIdent.class.cast(rel.getTilPerson().getAktoer()).getIdent();
        if (RequestUtils.isFnr(id)) {
            return new Fødselsnummer(id.getIdent());
        }
        else {
            return null;
        }
    }

    private HentPersonResponse hentPerson(HentPersonRequest request) {
        try {
            return person.hentPerson(request);
        } catch (HentPersonPersonIkkeFunnet e) {
            LOG.warn("Fant ikke person", e);
            throw new NotFoundException(e);
        } catch (HentPersonSikkerhetsbegrensning e) {
            LOG.warn("Sikkerhetsbegrensning ved oppslag.", e);
            throw new ForbiddenException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [person=" + person + "]";
    }

}
