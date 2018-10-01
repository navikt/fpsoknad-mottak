package no.nav.foreldrepenger.mottak.http;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.innsending.pdf.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.util.FnrExtractor;

@Service
@ConditionalOnProperty(name = "oppslag.stub", havingValue = "false", matchIfMissing = true)
public class OppslagTjeneste implements Oppslag {

    private final OppslagConnection connection;
    private final FnrExtractor extractor;

    public OppslagTjeneste(OppslagConnection connection, FnrExtractor extractor) {
        this.connection = connection;
        this.extractor = extractor;
    }

    @Override
    public Person getSøker() {
        return connection.getSøker();
    }

    @Override
    public AktorId getAktørId() {
        return getAktørId(new Fødselsnummer(extractor.fnrFromToken()));
    }

    @Override
    public AktorId getAktørId(Fødselsnummer fnr) {
        return connection.getAktørId(fnr);
    }

    @Override
    public Fødselsnummer getFnr(AktorId aktørId) {
        return connection.getFnr(aktørId);
    }

    @Override
    public List<Arbeidsforhold> getArbeidsforhold() {
        return connection.getArbeidsforhold();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", extractor=" + extractor + "]";
    }
}
