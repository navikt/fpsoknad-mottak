package no.nav.foreldrepenger.oppslag.person;

import static no.nav.foreldrepenger.oppslag.person.RequestUtils.BARN;
import static no.nav.foreldrepenger.oppslag.person.RequestUtils.FNR;
import static no.nav.foreldrepenger.oppslag.person.RequestUtils.request;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.ADRESSE;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.FAMILIERELASJONER;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.oppslag.domain.Barn;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Person;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.NotFoundException;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

public class PersonClient {

	private static final Logger LOG = LoggerFactory.getLogger(PersonClient.class);

	private final PersonV3 person;
	private final Barnutvelger barneVelger;

	public PersonClient(PersonV3 person, Barnutvelger barneVelger) {
		this.person = Objects.requireNonNull(person);
		this.barneVelger = Objects.requireNonNull(barneVelger);
	}

	public Person hentPersonInfo(ID id) {

		HentPersonRequest request = request(id.getFnr(), ADRESSE, FAMILIERELASJONER);
		return PersonMapper.map(id, hentPerson(id.getFnr(), request).getPerson(),barnFor(hentPerson(id.getFnr(),request).getPerson()));

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
			LOG.warn("Sikkerhetsbegrensning ved oppslag av {}", fnr.getFnr(), e);
			throw new ForbiddenException(e);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [person=" + person + ", barneVelger=" + barneVelger + "]";
	}

}
