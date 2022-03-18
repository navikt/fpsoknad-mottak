package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.time.LocalDate.now;
import static java.util.Comparator.comparing;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.ARBEIDSFORHOLD;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto.ArbeidsforholdDTO;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto.ArbeidsgiverDTO;

@Component
public class ArbeidsforholdConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdConnection.class);
    private final ArbeidsforholdConfig cfg;
    private final OrganisasjonConnection organisasjon;

    public ArbeidsforholdConnection(@Qualifier(ARBEIDSFORHOLD) WebClient client, ArbeidsforholdConfig cfg,
                                    OrganisasjonConnection organisasjon) {
        super(client, cfg);
        this.cfg = cfg;
        this.organisasjon = organisasjon;
    }

    List<EnkeltArbeidsforhold> hentArbeidsforhold() {
        return hentArbeidsforhold(now().minus(cfg.getTidTilbake()));
    }

    private List<EnkeltArbeidsforhold> hentArbeidsforhold(LocalDate fom) {
        LOG.info("Henter arbeidsforhold for perioden fra {}", fom);
        var arbeidsforhold = webClient.get()
            .uri(b -> cfg.getArbeidsforholdURI(b, fom))
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(ArbeidsforholdDTO.class)
            .mapNotNull(this::tilEnkeltArbeidsforhold)
            .sort(comparing(EnkeltArbeidsforhold::getArbeidsgiverNavn))
            .collectList()
            .block();

        LOG.info("Hentet {} arbeidsforhold for perioden fra {}", arbeidsforhold.size(), fom);
        LOG.trace("Arbeidsforhold: {}", arbeidsforhold);
        return arbeidsforhold;
    }

    private EnkeltArbeidsforhold tilEnkeltArbeidsforhold(ArbeidsforholdDTO a) {
        var arbeidsgiverId = tilArbeidsgiverId(a.arbeidsgiver());
        return EnkeltArbeidsforhold.builder()
            .arbeidsgiverId(arbeidsgiverId)
            .from(a.ansettelsesperiode().periode().fom())
            .to(Optional.ofNullable(a.ansettelsesperiode().periode().tom()))
            .stillingsprosent(a.gjeldendeStillingsprosent())
            .arbeidsgiverNavn(organisasjon.navn(arbeidsgiverId))
            .build();
    }

    private String tilArbeidsgiverId(ArbeidsgiverDTO arbeidsgiver) {
        if (arbeidsgiver.type() == null) {
            throw new IllegalArgumentException("Arbeidsgiver er hverken av typen organisasjon eller privatperson. Noe er galt!");
        }
        return switch (arbeidsgiver.type()) {
            case Organisasjon -> arbeidsgiver.organisasjonsnummer().value();
            case Person -> arbeidsgiver.offentligIdent().value();
        };
    }


    @Override
    public String name() {
        return capitalize(ARBEIDSFORHOLD.toLowerCase());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + ", webClient=" + webClient + ", name=" + name() + "]";
    }

}
