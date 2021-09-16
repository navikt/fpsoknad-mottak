package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static no.nav.foreldrepenger.common.util.Constants.FNR;
import static no.nav.foreldrepenger.mottak.util.MapUtil.get;
import static no.nav.foreldrepenger.mottak.util.TimeUtil.dato;
import static no.nav.foreldrepenger.mottak.util.TimeUtil.nowWithinPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.util.Pair;

@Component
class ArbeidsforholdMapper {

    private static final String PERIODE = "periode";
    private static final String ARBEIDSAVTALER = "arbeidsavtaler";
    private static final String TYPE = "type";
    private static final String OFFENTLIG_IDENT = "offentligIdent";
    private static final String PERSON = "Person";
    private static final String ORGNR = "orgnr";
    private static final String ORGANISASJON = "Organisasjon";
    private static final String STILLINGSPROSENT = "stillingsprosent";
    private static final String GYLDIGHETSPERIODE = "gyldighetsperiode";
    private static final String TOM = "tom";
    private static final String ORGANISASJONSNUMMER = "organisasjonsnummer";
    private static final String FOM = "fom";
    private static final String ARBEIDSGIVER = "arbeidsgiver";
    private static final String ANSETTELSESPERIODE = "ansettelsesperiode";
    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdMapper.class);
    private final OrganisasjonConnection organisasjon;

    ArbeidsforholdMapper(OrganisasjonConnection organisasjon) {
        this.organisasjon = organisasjon;
    }

    EnkeltArbeidsforhold tilArbeidsforhold(Map<?, ?> map) {
        var id = id(get(map, ARBEIDSGIVER, Map.class));
        var periode = get(get(map, ANSETTELSESPERIODE, Map.class), PERIODE, Map.class);
        return EnkeltArbeidsforhold.builder()
                .arbeidsgiverId(id.getFirst())
                .arbeidsgiverIdType(id.getSecond())
                .from(dato(get(periode, FOM)))
                .to(Optional.ofNullable(dato(get(periode, TOM))))
                .stillingsprosent(stillingsprosent(get(map, ARBEIDSAVTALER, List.class)))
                .arbeidsgiverNavn(organisasjon.navn(id.getFirst()))
                .build();
    }

    private static ProsentAndel stillingsprosent(List<?> avtaler) {
        return avtaler.stream()
                .map(Map.class::cast)
                .filter(ArbeidsforholdMapper::erGjeldende)
                .map(a -> get(a, STILLINGSPROSENT, Double.class))
                .filter(Objects::nonNull)
                .map(ProsentAndel::new)
                .findFirst()
                .orElse(null);
    }

    private static boolean erGjeldende(Map<?, ?> avtale) {
        var periode = get(avtale, GYLDIGHETSPERIODE, Map.class);
        var fom = dato(get(periode, FOM));
        if (fom.isAfter(LocalDate.now())) {
            LOG.trace("Fremtidig arbeidsforhold begynner {}", fom);
            return true;
        }
        var tom = Optional.ofNullable(dato(get(periode, TOM)))
                .orElse(LocalDate.now());
        var gyldig = nowWithinPeriod(fom, tom);
        LOG.trace("Arbeidsorhold gyldig: {}", gyldig);
        return gyldig;
    }

    private static Pair<String, String> id(Map<?, ?> map) {
        return switch (get(map, TYPE)) {
            case ORGANISASJON -> Pair.of(get(map, ORGANISASJONSNUMMER), ORGNR);
            case PERSON -> Pair.of(get(map, OFFENTLIG_IDENT), FNR);
            default -> throw new IllegalArgumentException("Fant verken orgnr eller fnr i " + map);
        };
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[organisasjon=" + organisasjon + "]";
    }

}
