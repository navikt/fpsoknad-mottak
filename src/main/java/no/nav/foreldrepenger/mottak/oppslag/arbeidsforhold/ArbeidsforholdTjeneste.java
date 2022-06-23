package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.util.Comparator.comparing;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.mottak.http.RetryAware;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto.ArbeidsforholdDTO;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto.ArbeidsgiverDTO;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto.ArbeidsgiverType;

@Service
public class ArbeidsforholdTjeneste implements RetryAware, ArbeidsInfo {
    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdTjeneste.class);
    private final ArbeidsforholdConnection connection;
    private final OrganisasjonConnection orgConnection;

    public ArbeidsforholdTjeneste(ArbeidsforholdConnection connection, OrganisasjonConnection orgConnection) {
        this.connection = connection;
        this.orgConnection = orgConnection;
    }

    @Override
    public List<EnkeltArbeidsforhold> hentArbeidsforhold() {
        var enkleArbeidsforhold = connection.hentArbeidsforhold().stream()
            .filter(Objects::nonNull)
            .map(this::tilEnkeltArbeidsforhold)
            .sorted(comparing(EnkeltArbeidsforhold::getArbeidsgiverNavn))
            .toList();
        LOG.trace("Arbeidsforhold: {}", enkleArbeidsforhold);
        return enkleArbeidsforhold;
    }

    @Override
    public String orgnavn(Orgnummer orgnr) {
        return orgConnection.navn(orgnr.value());
    }

    public EnkeltArbeidsforhold tilEnkeltArbeidsforhold(ArbeidsforholdDTO a) {
        var arbeidsgiverId = tilArbeidsgiverId(a.arbeidsgiver());
        return EnkeltArbeidsforhold.builder()
            .arbeidsgiverId(arbeidsgiverId)
            .arbeidsgiverIdType(tilArbeidsgiverTypeFrontend(a.arbeidsgiver().type()))
            .from(a.ansettelsesperiode().periode().fom())
            .to(Optional.ofNullable(a.ansettelsesperiode().periode().tom()))
            .stillingsprosent(a.gjeldendeStillingsprosent())
            .arbeidsgiverNavn(orgConnection.navn(arbeidsgiverId))
            .build();
    }

    private String tilArbeidsgiverTypeFrontend(ArbeidsgiverType type) {
        return switch (type) {
            case ORGANISASJON -> "orgnr";
            case PERSON -> "fnr";
        };
    }

    private String tilArbeidsgiverId(ArbeidsgiverDTO arbeidsgiver) {
        if (arbeidsgiver.type() == null) {
            throw new IllegalArgumentException("Arbeidsgiver er hverken av typen organisasjon eller privatperson. Noe er galt!");
        }
        return switch (arbeidsgiver.type()) {
            case ORGANISASJON -> arbeidsgiver.organisasjonsnummer().value();
            case PERSON -> arbeidsgiver.offentligIdent().value();
        };
    }


    @Override
    public String ping() {
        return connection.ping();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", orgConnection=" + orgConnection + "]";
    }
}
