package no.nav.foreldrepenger.oppslag.person;

import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.ADRESSE;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.FAMILIERELASJONER;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.oppslag.domain.Adresse;
import no.nav.foreldrepenger.oppslag.domain.Barn;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Name;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.NotFoundException;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
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

	private final PersonV3 person;
	private final Barnutvelger barneVelger;

	public PersonKlient(PersonV3 person, Barnutvelger barneVelger) {
		this.person = Objects.requireNonNull(person);
		this.barneVelger = Objects.requireNonNull(barneVelger);
	}

	public no.nav.foreldrepenger.oppslag.domain.Person hentPersonInfo(ID id) {
		try {
			return person(id,
			        person.hentPerson(RequestUtils.request(id.getFnr(), ADRESSE, FAMILIERELASJONER)).getPerson());
		} catch (HentPersonPersonIkkeFunnet e) {
			LOG.warn("Unable to fetch information for user {}", id.getFnr(), e);
			throw new NotFoundException(e);
		} catch (HentPersonSikkerhetsbegrensning e) {
			LOG.warn("Unable to fetch information for user {}", id.getFnr(), e);
			throw new ForbiddenException(e);
		}
	}

	private no.nav.foreldrepenger.oppslag.domain.Person person(ID id,
	        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
		return new no.nav.foreldrepenger.oppslag.domain.Person(id, name(person.getPersonnavn()),
		        address(person.getBostedsadresse().getStrukturertAdresse()), birthDate(person), barnFor(person));
	}

	private LocalDate birthDate(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
		return CalendarConverter.toDate(person.getFoedselsdato().getFoedselsdato());
	}

	private List<Barn> barnFor(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
		PersonIdent id = PersonIdent.class.cast(person.getAktoer());
		String idType = id.getIdent().getType().getValue();
		switch (idType) {
		case RequestUtils.FNR:
			Fodselsnummer fnrMor = new Fodselsnummer(id.getIdent().getIdent());
			return person.getHarFraRolleI().stream().filter(this::isBarn).map(s -> hentBarn(s, fnrMor))
			        .filter(barn -> barneVelger.erStonadsberettigetBarn(fnrMor, barn)).collect(Collectors.toList());
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
				HentPersonResponse barn = person.hentPerson(RequestUtils.request(fnrBarn, FAMILIERELASJONER));
				return new Barn(fnrMor, new Fodselsnummer(id.getIdent()), birthDate(barn.getPerson()));
			} catch (HentPersonPersonIkkeFunnet e) {
				LOG.warn("Barn {} av mor {} ble ikke funnet i TPS", fnrBarn, fnrMor);
				throw new NotFoundException(
				        "Barn " + fnrBarn.getFnr() + " av mor " + fnrMor.getFnr() + "ble ikke funnet");
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
