package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import static no.nav.foreldrepenger.oppslag.lookup.ws.person.RequestUtils.BARN;
import static no.nav.foreldrepenger.oppslag.lookup.ws.person.RequestUtils.DNR;
import static no.nav.foreldrepenger.oppslag.lookup.ws.person.RequestUtils.FNR;
import static no.nav.foreldrepenger.oppslag.lookup.ws.person.RequestUtils.request;
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
import no.nav.foreldrepenger.oppslag.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.oppslag.errorhandling.NotFoundException;
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

    private static final Counter ERROR_COUNTER = Metrics.counter("person.lookup.error");

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
            LOG.info("Doing person lookup");
            HentPersonRequest request = RequestUtils.request(id.getFnr(), KOMMUNIKASJON, BANKKONTO, FAMILIERELASJONER);
            no.nav.tjeneste.virksomhet.person.v3.informasjon.Person tpsPerson = hentPerson(id.getFnr(), request)
                    .getPerson();
            return PersonMapper.map(id, tpsPerson, barnFor(tpsPerson));
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw new RuntimeException(ex);
        }

    }

    private List<Barn> barnFor(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        PersonIdent id = PersonIdent.class.cast(person.getAktoer());
        String idType = id.getIdent().getType().getValue();
        switch (idType) {
        case FNR:
        case DNR:
            Fodselsnummer fnrMor = new Fodselsnummer(id.getIdent().getIdent());
            return person.getHarFraRolleI().stream()
                    .filter(this::isBarn)
                    .map(s -> hentBarn(s, fnrMor))
                    .filter(Objects::nonNull)
                    .filter(barn -> barnutvelger.erStonadsberettigetBarn(fnrMor, barn))
                    .collect(Collectors.toList());
        default:
            throw new IllegalStateException("ID type " + idType + " ikke støttet");
        }
    }

    private boolean isBarn(Familierelasjon rel) {
        return rel.getTilRolle().getValue().equals(BARN);
    }

    private Barn hentBarn(Familierelasjon rel, Fodselsnummer fnrMor) {
        NorskIdent id = PersonIdent.class.cast(rel.getTilPerson().getAktoer()).getIdent();
        if (RequestUtils.isFnr(id)) {
            Fodselsnummer fnrBarn = new Fodselsnummer(id.getIdent());
            return PersonMapper.map(id, fnrMor, hentPerson(fnrBarn, request(fnrBarn, FAMILIERELASJONER)));
        }
        return null;
    }

    private HentPersonResponse hentPerson(Fodselsnummer fnr, HentPersonRequest request) {
        try {
            return person.hentPerson(request);
        } catch (HentPersonPersonIkkeFunnet e) {
            LOG.warn("Fant ikke person {}", fnr, e);
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
