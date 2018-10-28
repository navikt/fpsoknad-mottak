package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ÅpenPeriode;

@Component
public class SøknadTextFormatter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.uuuu");

    private final MessageSource landkoder;
    private final MessageSource kvitteringstekster;
    private final Locale locale;

    @Inject
    public SøknadTextFormatter(@Qualifier("landkoder") MessageSource landkoder,
            @Qualifier("kvitteringstekster") MessageSource kvitteringstekster) {
        this(landkoder, kvitteringstekster, CountryCode.NO.toLocale());
    }

    private SøknadTextFormatter(MessageSource landkoder, MessageSource kvitteringstekster, Locale locale) {
        this.landkoder = landkoder;
        this.kvitteringstekster = kvitteringstekster;
        this.locale = locale;
    }

    public String countryName(String isoCode, Object... values) {
        return Optional.ofNullable(getMessage(isoCode, landkoder, values)).orElse(isoCode);
    }

    public String fromMessageSource(String key, Object... values) {
        return getMessage(key, kvitteringstekster, values);
    }

    public String navn(Navn navn) {
        String sammensattnavn = Joiner.on(' ')
                .skipNulls()
                .join(navn.getFornavn(), navn.getMellomnavn(), navn.getEtternavn());
        return sammensattnavn.isEmpty() ? "" : fromMessageSource("navn", sammensattnavn);
    }

    public String dato(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(s -> s.format(DATE_FMT))
                .orElse("");
    }

    public String datoer(List<LocalDate> datoer) {
        return datoer.stream()
                .map(this::dato)
                .collect(joining(", "));
    }

    public String countryName(boolean b) {
        return b ? "Norge" : "utlandet";
    }

    public String navn(Person søker) {
        return Optional.ofNullable(søker)
                .map(s -> Joiner.on(' ').skipNulls().join(s.fornavn, s.mellomnavn, s.etternavn))
                .map(String::trim)
                .orElse("Ukjent");
    }

    public String yesNo(boolean b) {
        return b ? "Ja" : "Nei";
    }

    public String periode(ÅpenPeriode periode) {
        StringBuilder sb = new StringBuilder("fra og med " + dato(periode.getFom()));
        if (periode.getTom() != null) {
            sb.append(periode.getTom() != null ? " til og med" + dato(periode.getTom()) : " pågående");
        }
        return sb.toString();
    }

    public String capitalize(String orig) {
        String lowerWithSpace = orig.replaceAll("_", " ").toLowerCase();
        return lowerWithSpace.substring(0, 1).toUpperCase() + lowerWithSpace.substring(1);
    }

    public List<String> utenlandsOpphold(List<Utenlandsopphold> opphold) {
        if (opphold.isEmpty()) {
            return Collections.singletonList(countryName(CountryCode.NO.getAlpha2()));
        }
        return opphold.stream()
                .map(this::formatOpphold)
                .collect(toList());
    }

    private String formatOpphold(Utenlandsopphold opphold) {
        return countryName(opphold.getLand().getAlpha2(), opphold.getLand().getName())
                + ": "
                + dato(opphold.getVarighet().getFom()) + " - "
                + dato(opphold.getVarighet().getTom());
    }

    private String getMessage(String key, MessageSource messages, Object... values) {
        return getMessage(key, null, messages, values);
    }

    private String getMessage(String key, String defaultValue, MessageSource messages, Object... values) {
        ((ResourceBundleMessageSource) messages).setDefaultEncoding("utf-8");
        return messages.getMessage(key, values, defaultValue, locale);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [landkoder=" + landkoder + ", kvitteringstekster=" + kvitteringstekster
                + ", locale=" + locale + "]";
    }
}
