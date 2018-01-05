package no.nav.foreldrepenger.selvbetjening.person.klient;

import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.ADRESSE;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.FAMILIERELASJONER;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.selvbetjening.domain.Adresse;
import no.nav.foreldrepenger.selvbetjening.domain.Barn;
import no.nav.foreldrepenger.selvbetjening.domain.Fodselsnummer;
import no.nav.foreldrepenger.selvbetjening.domain.ID;
import no.nav.foreldrepenger.selvbetjening.domain.Name;
import no.nav.foreldrepenger.selvbetjening.domain.Person;
import no.nav.foreldrepenger.selvbetjening.domain.exceptions.NotFoundException;
import no.nav.foreldrepenger.selvbetjening.domain.exceptions.ForbiddenException;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Gateadresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personnavn;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.StrukturertAdresse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

public class PersonKlient {

	private static final Logger LOG = LoggerFactory.getLogger(PersonKlient.class);

	private final PersonV3 personV3;
	private final BarneVelger childSelector;

	public PersonKlient(PersonV3 personV3, BarneVelger childSelector) {
		this.personV3 = Objects.requireNonNull(personV3);
		this.childSelector = Objects.requireNonNull(childSelector);
	}
	

	public <Optional> Person hentPersonInfo(ID id)  {
		try {
			return person(id,personV3.hentPerson(RequestUtils.request(id.getFnr(), ADRESSE, FAMILIERELASJONER)).getPerson());
		} catch (HentPersonPersonIkkeFunnet e) {
			LOG.warn("Unable to fetch information for user {}", id.getFnr(), e);
			throw new NotFoundException(e);
		}
		catch (HentPersonSikkerhetsbegrensning e) {
			LOG.warn("Unable to fetch information for user {}", id.getFnr(), e);
			throw new ForbiddenException(e);
		}
	}

	private Person person(ID id, no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
		return new Person(id, name(person.getPersonnavn()), address(person.getBostedsadresse().getStrukturertAdresse()),
		        birthDate(person), barnFor(person));
	}

	private LocalDate birthDate(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
		return LocalDate.fromCalendarFields(person.getFoedselsdato().getFoedselsdato().toGregorianCalendar());
	}

	private List<Barn> barnFor(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
		PersonIdent id = PersonIdent.class.cast(person.getAktoer());
		String idType = id.getIdent().getType().getValue();
		switch (idType) {
		case RequestUtils.FNR:
			Fodselsnummer fnrMor = new Fodselsnummer(id.getIdent().getIdent());
			return person.getHarFraRolleI().stream().filter(this::isBarn).map(s -> hentBarn(s, fnrMor))
			        .filter(barn -> childSelector.isEligible(fnrMor, barn)).collect(Collectors.toList());
		default:
			throw new IllegalStateException("ID type " + idType + " not yet supported");
		}
	}

	private boolean isBarn(Familierelasjon rel) {
		return rel.getTilRolle().getValue().equals(RequestUtils.BARN);
	}

	private Barn hentBarn(Familierelasjon rel, Fodselsnummer fnrMor) {
		NorskIdent id = PersonIdent.class.cast(rel.getTilPerson().getAktoer()).getIdent();
		if (RequestUtils.isFnr(id)) {
			Fodselsnummer fnrBarn = new Fodselsnummer(id.getIdent());
			try {
				HentPersonResponse barn = personV3.hentPerson(RequestUtils.request(fnrBarn, FAMILIERELASJONER));
				return new Barn(fnrMor, new Fodselsnummer(id.getIdent()), birthDate(barn.getPerson()));
			} catch (HentPersonPersonIkkeFunnet e) {
				LOG.warn("Barn {} av mor {} ble ikke funnet i TPS", fnrBarn, fnrMor);
				throw new NotFoundException("Barn " + fnrBarn.getFnr() + " av mor " + fnrMor.getFnr() + "ble ikke funnet");
			} catch (HentPersonSikkerhetsbegrensning e) {
				LOG.warn("Oppslag på barn {} av mor {} ikke tillatt", fnrBarn, fnrMor);
				throw new ForbiddenException(e);
			}
		}
		throw new IllegalStateException("ID type " + id.getType().getValue() + " not yet supported");
	}

	private static Adresse address(StrukturertAdresse adresse) {
		if (adresse instanceof Gateadresse) {
			Gateadresse ga = Gateadresse.class.cast(adresse);
			if (ga.getTilleggsadresseType().equalsIgnoreCase(RequestUtils.OFFISIELL_ADRESSE)) {

				return new Adresse(ga.getLandkode().getValue(), ga.getPoststed().getValue(), ga.getGatenavn(),
				        ga.getHusnummer().toString(), ga.getHusbokstav());
			}
			throw new IllegalStateException("Address av type " + ga.getTilleggsadresseType() + " ikke støttet");
		}
		throw new IllegalStateException("Address av type " + adresse.getClass().getSimpleName() + " ikke støttet");
	}

	private static Name name(Personnavn navn) {
		return new Name(navn.getFornavn(), navn.getMellomnavn(), navn.getEtternavn());
	}

}
