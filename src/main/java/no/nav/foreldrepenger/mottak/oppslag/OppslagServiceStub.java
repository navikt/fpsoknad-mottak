package no.nav.foreldrepenger.mottak.oppslag;

import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.DEV;
import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.LOCAL;

import java.time.LocalDate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;

@Service
@Profile({ DEV, LOCAL })
@ConditionalOnProperty(name = "oppslag.stub", havingValue = "true")
public class OppslagServiceStub implements Oppslag {
    @Override
    public Person søker() {
        return person();
    }

    @Override
    public AktørId aktørId() {
        return new AktørId("11111111111");
    }

    @Override
    public AktørId aktørId(Fødselsnummer fnr) {
        return new AktørId("11111111111");
    }

    @Override
    public Fødselsnummer fnr(AktørId aktørId) {
        return new Fødselsnummer("01010111111");
    }

    private static Person person() {
        return new Person(new Fødselsnummer("010101010101"), "Mor", "Mellommor", "Morsen", Kjønn.K,
                LocalDate.now().minusYears(25), "NN",
                CountryCode.NO, false,
                new Bankkonto("2000.20.20000", "Store Fiskerbank"));
    }

    @Override
    public String ping() {
        return "42";
    }

    @Override
    public Navn navn(Fødselsnummer fnr) {
        return new Navn("Ole", "Mellom", "Olsen", Kjønn.M);
    }
}
