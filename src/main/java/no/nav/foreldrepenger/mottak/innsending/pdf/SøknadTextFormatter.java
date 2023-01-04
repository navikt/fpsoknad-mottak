package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.stream.Collectors.joining;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.config.MottakConfiguration.KVITTERINGSTEKSTER;
import static no.nav.foreldrepenger.mottak.config.MottakConfiguration.LANDKODER;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.common.domain.felles.ÅpenPeriode;

@Component
public class SøknadTextFormatter {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.uuuu");
    private final MessageSource landkoder;
    private final MessageSource kvitteringstekster;
    private final Locale locale;

    @Inject
    public SøknadTextFormatter(@Qualifier(LANDKODER) MessageSource landkoder,
                               @Qualifier(KVITTERINGSTEKSTER) MessageSource kvitteringstekster) {
        this(landkoder, kvitteringstekster, CountryCode.NO.toLocale());
    }

    private SøknadTextFormatter(MessageSource landkoder, MessageSource kvitteringstekster, Locale locale) {
        this.landkoder = landkoder;
        this.kvitteringstekster = kvitteringstekster;
        this.locale = locale;
    }

    public String countryName(CountryCode code, Object... values) {
        return countryName(code.getAlpha2(), values);
    }

    private String countryName(String isoCode, Object... values) {
        return Optional.ofNullable(getMessage(isoCode, landkoder, values)).orElse(isoCode);
    }

    public String fromMessageSource(String key, Object... values) {
        return getMessage(key, kvitteringstekster, values);
    }

    public String navn(String navn) {
        return fromMessageSource("navninline", navn);
    }

    public String navn(Navn navn) {
        var sammensattnavn = sammensattNavn(navn);
        return sammensattnavn.isEmpty() ? "" : fromMessageSource("navninline", sammensattnavn);
    }

    public String navn(Person søker) {
        return Optional.ofNullable(søker)
                .map(this::sammensattNavn)
                .map(String::trim)
                .orElse("Ukjent");
    }

    public String sammensattNavn(Navn navn) {
        return sammensattNavn(navn.fornavn(), navn.mellomnavn(), navn.etternavn());
    }

    public String sammensattNavn(Person person) {
        return sammensattNavn(person.getFornavn(), person.getMellomnavn(), person.getEtternavn());
    }

    private String sammensattNavn(String fornavn, String mellomnavn, String etternavn) {
        var builder = new StringBuilder();
        leggTilNavn(builder, fornavn);
        leggTilNavn(builder, mellomnavn);
        leggTilNavn(builder, etternavn);
        return builder.toString();
    }

    private static void leggTilNavn(StringBuilder stringBuilder, String navn) {
        if (navn != null && !navn.isEmpty()) {
            if (!stringBuilder.isEmpty()) stringBuilder.append(' ');
            stringBuilder.append(navn);
        }
    }

    public String dato(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(s -> s.format(DATE_FMT))
                .orElse("");
    }

    public String datoer(List<LocalDate> datoer) {
        return safeStream(datoer)
                .map(this::dato)
                .collect(joining(", "));
    }

    public String yesNo(boolean b) {
        return b ? "Ja" : "Nei";
    }

    public String periode(ÅpenPeriode periode) {
        var sb = new StringBuilder("fra og med " + dato(periode.fom()));
        if (periode.tom() != null) {
            sb.append(" til og med ").append(dato(periode.tom()));
        }
        return sb.toString();
    }

    public String enkelPeriode(ÅpenPeriode periode) {
        var sb = new StringBuilder(dato(periode.fom()));
        if (periode.tom() != null) {
            sb.append(" - ").append(dato(periode.tom()));
        }
        return sb.toString();
    }

    public String capitalize(String orig) {
        String lowerWithSpace = orig.replace("_", " ").toLowerCase();
        return lowerWithSpace.substring(0, 1).toUpperCase() + lowerWithSpace.substring(1);
    }

    public List<String> utenlandsOpphold(List<Utenlandsopphold> opphold) {
        if (CollectionUtils.isEmpty(opphold)) {
            return Collections.singletonList(countryName(CountryCode.NO));
        }
        return safeStream(opphold)
                .map(this::formatOpphold)
                .toList();
    }

    private String formatOpphold(Utenlandsopphold opphold) {
        return countryName(opphold.land(), opphold.land().getName())
                + ": "
                + dato(opphold.fom()) + " - "
                + dato(opphold.tom());
    }

    private String getMessage(String key, MessageSource messages, Object... values) {
        return getMessage(key, null, messages, values);
    }

    private String getMessage(String key, String defaultValue, MessageSource messages, Object... values) {
        ((ResourceBundleMessageSource) messages).setDefaultEncoding("utf-8");
        return messages.getMessage(key, values, defaultValue, locale);
    }

    public List<Pair<String, String>> utenlandsPerioder(List<Utenlandsopphold> opphold) {
        if (CollectionUtils.isEmpty(opphold)) {
            return Collections.singletonList(Pair.of(countryName(CountryCode.NO), null));
        }
        return safeStream(opphold)
                .map(o -> Pair.of(countryName(o.land(), o.land().getName()), dato(o.fom(), o.tom())))
                .collect(Collectors.toList());
    }

    private String dato(LocalDate fom, LocalDate tom) {
        return dato(fom) + " – " + dato(tom);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [landkoder=" + landkoder + ", kvitteringstekster=" + kvitteringstekster
                + ", locale=" + locale + "]";
    }
}
