package no.nav.foreldrepenger.oppslag.person;

import static no.nav.foreldrepenger.oppslag.person.RequestUtils.BARN;
import static no.nav.foreldrepenger.oppslag.person.RequestUtils.FNR;
import static no.nav.foreldrepenger.oppslag.person.RequestUtils.request;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.oppslag.domain.Barn;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Person;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.NotFoundException;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

public class PersonClientTpsWs implements PersonClient {

    private static final Logger LOG = LoggerFactory.getLogger(PersonClientTpsWs.class);

    private final PersonV3 person;
    private final Barnutvelger barneVelger;

    private final Counter errorCounter = Metrics.counter("errors.lookup.person");
    private final Counter missingLanguageCounter = Metrics.counter("lookup.user.missinglanguage");
    private final Counter bankkontoCounter = Metrics.counter("lookup.user.haskonto");

    public PersonClientTpsWs(PersonV3 person, Barnutvelger barneVelger) {
        this.person = Objects.requireNonNull(person);
        this.barneVelger = Objects.requireNonNull(barneVelger);
    }

    @Override
    public void ping() {
        person.ping();
    }

    @Override
    public Person hentPersonInfo(ID id) {

        try {
            HentPersonRequest request = request(id.getFnr(), ADRESSE, KOMMUNIKASJON, BANKKONTO, FAMILIERELASJONER);
            no.nav.tjeneste.virksomhet.person.v3.informasjon.Person tpsPerson =
                hentPerson(id.getFnr(), request).getPerson();
            Person person = PersonMapper.map(id, tpsPerson, barnFor(tpsPerson));
            collectLanguageMetrics(person);
            collectBankkontoMetrics(tpsPerson);
            return person;
        } catch (Exception ex) {
            errorCounter.increment();
            throw new RuntimeException(ex);
        }

    }

    private List<Barn> barnFor(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        PersonIdent id = PersonIdent.class.cast(person.getAktoer());
        String idType = id.getIdent().getType().getValue();
        switch (idType) {
        case FNR:
            Fodselsnummer fnrMor = new Fodselsnummer(id.getIdent().getIdent());
            return person.getHarFraRolleI().stream().filter(this::isBarn).map(s -> hentBarn(s, fnrMor))
                    .filter(barn -> barneVelger.erStonadsberettigetBarn(fnrMor, barn)).collect(Collectors.toList());
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
            return PersonMapper.map(id, fnrMor, hentPerson(fnrBarn, FAMILIERELASJONER));
        }
        throw new IllegalStateException("ID type " + id.getType().getValue() + " ikke støttet");
    }

    private HentPersonResponse hentPerson(Fodselsnummer fnr, Informasjonsbehov... behov) {
        return hentPerson(fnr, request(fnr, behov));
    }

    private HentPersonResponse hentPerson(Fodselsnummer fnr, HentPersonRequest request) {
        try {
            return person.hentPerson(request);
        } catch (HentPersonPersonIkkeFunnet e) {
            LOG.warn("Kunne ikke slå opp person " + "{}", fnr.getFnr(), e);
            throw new NotFoundException(e);
        } catch (HentPersonSikkerhetsbegrensning e) {
            LOG.warn("Sikkerhetsbegrensning ved oppslag.", e);
            throw new ForbiddenException(e);
        }
    }

    private void collectLanguageMetrics(Person person) {
        if (person.getMålform() == null) {
            missingLanguageCounter.increment();
        }
        LOG.info("Målform: " + person.getMålform() != null ? person.getMålform() : "ukjent");
    }

    private void collectBankkontoMetrics(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        if (person instanceof Bruker) {
            Bruker bruker = Bruker.class.cast(person);
            if (bruker.getBankkonto() != null) {
                bankkontoCounter.increment();
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [person=" + person + ", barneVelger=" + barneVelger + "]";
    }

}
