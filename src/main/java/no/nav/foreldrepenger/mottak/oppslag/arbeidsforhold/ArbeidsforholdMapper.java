package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static no.nav.foreldrepenger.mottak.util.MapUtil.get;
import static no.nav.foreldrepenger.mottak.util.TimeUtil.dateWithinPeriod;
import static no.nav.foreldrepenger.mottak.util.TimeUtil.dato;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.util.MapUtil;
import no.nav.foreldrepenger.mottak.util.Pair;

@Component
class ArbeidsforholdMapper {

    private static final String PERIODE2 = "periode";
    private static final String ARBEIDSAVTALER = "arbeidsavtaler";
    private static final String TYPE = "type";
    private static final String FNR2 = "fnr";
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

    public ArbeidsforholdMapper(OrganisasjonConnection organisasjon) {
        this.organisasjon = organisasjon;
    }

    EnkeltArbeidsforhold map(Map<?, ?> map) {
        var id = id(get(map, ARBEIDSGIVER, Map.class));
        var periode = get(get(map, ANSETTELSESPERIODE, Map.class), PERIODE2, Map.class);
        return new EnkeltArbeidsforhold(id.getFirst(), id.getSecond(), dato(get(periode, FOM)),
                Optional.ofNullable(dato(get(periode, TOM))),
                stillingsprosent(get(map, ARBEIDSAVTALER, List.class)), navn(id.getFirst()));
    }

    private String navn(String orgnr) {
        try {
            return organisasjon.organisasjonsNavn(orgnr);
        } catch (Exception e) {
            LOG.warn("OOPS", e);
            return orgnr;
        }
    }

    private static ProsentAndel stillingsprosent(List<?> avtaler) {
        return avtaler.stream()
                .map(Map.class::cast)
                .filter(ArbeidsforholdMapper::gjeldendeAvtale)
                .map(a -> MapUtil.get(a, STILLINGSPROSENT, Double.class))
                .filter(Objects::nonNull)
                .map(ProsentAndel::new)
                .findFirst()
                .orElse(null);
    }

    private static boolean gjeldendeAvtale(Map<?, ?> avtale) {
        var periode = get(avtale, GYLDIGHETSPERIODE, Map.class);
        return dateWithinPeriod(dato(get(periode, FOM)), Optional.ofNullable(dato(get(periode, TOM)))
                .orElse(LocalDate.now()));
    }

    private static Pair<String, String> id(Map<?, ?> map) {
        var type = get(map, TYPE);
        if (ORGANISASJON.equals(type)) {
            return Pair.of(get(map, ORGANISASJONSNUMMER), ORGNR);
        }
        if (PERSON.equals(type)) {
            return Pair.of(get(map, OFFENTLIG_IDENT), FNR2);
        }
        throw new IllegalArgumentException("Fant verken orgnr eller fnr i " + map);
    }

}
