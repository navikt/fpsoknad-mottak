package no.nav.foreldrepenger.mottak.pdf;

import static java.util.stream.Collectors.joining;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ÅpenPeriode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Regnskapsfører;

class SøknadInfoFormatter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.uuuu");

    private final MessageSource landkoder;

    private final MessageSource kvitteringstekster;
    private final Locale locale;

    public SøknadInfoFormatter(@Qualifier("landkoder") MessageSource landkoder,
            @Qualifier("kvitteringstekster") MessageSource kvitteringstekster,
            Locale locale) {
        this.landkoder = landkoder;
        this.kvitteringstekster = kvitteringstekster;
        this.locale = locale;
    }

    public String countryName(String isoCode, Object... values) {
        return getMessage(isoCode, landkoder, values);
    }

    public String fromMessageSource(String key, Object... values) {
        return getMessage(key, kvitteringstekster, values);
    }

    public String navnToString(List<Regnskapsfører> regnskapsførere) {
        return regnskapsførere == null ? "ukjent"
                : regnskapsførere.stream()
                        .map(Regnskapsfører::getNavn)
                        .collect(joining(","));
    }

    public String navnToString(Navn navn) {
        return (formatNavn(navn.getFornavn()) + " "
                + formatNavn(navn.getMellomnavn()) + " "
                + formatNavn(navn.getEtternavn()) + " ").trim();
    }

    private static String formatNavn(String navn) {
        return Optional.ofNullable(navn).orElse("");
    }

    public String dato(LocalDate localDate) {
        return localDate != null ? localDate.format(DATE_FMT) : "?";
    }

    public String dato(List<LocalDate> dates) {
        return dates.stream()
                .map(this::dato)
                .collect(joining(", "));
    }

    public String countryName(Boolean b) {
        return b ? "Norge" : "utlandet";
    }

    public String navn(Navn navn) {
        String n = navnToString(navn);
        return n.isEmpty() ? "" : fromMessageSource("navn", n);
    }

    public String navn(Person søker) {
        if (søker == null) {
            return null;
        }
        return (Optional.ofNullable(søker.fornavn).orElse("ukjent") + " "
                + Optional.ofNullable(søker.mellomnavn).orElse("u") + " "
                + Optional.ofNullable(søker.etternavn).orElse("ukjentsen")).trim();
    }

    public String yesNo(boolean b) {
        return b ? "Ja" : "Nei";
    }

    public String periode(ÅpenPeriode periode) {
        StringBuilder sb = new StringBuilder("fom " + dato(periode.getFom()));
        if (periode.getTom() != null) {
            sb.append(" tom " + dato(periode.getTom()));
        }
        return sb.toString();
    }

    public String capitalize(String orig) {
        String lowerWithSpace = orig.toString().replaceAll("_", " ").toLowerCase();
        return lowerWithSpace.substring(0, 1).toUpperCase() + lowerWithSpace.substring(1);
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
