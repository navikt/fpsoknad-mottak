package no.nav.foreldrepenger.mottak.pdf;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Regnskapsfører;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class SøknadInfoFormatter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.uuuu");

    private MessageSource landkoder;
    private MessageSource kvitteringstekster;
    private Locale locale;

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
        return regnskapsførere == null ? "ukjent" : regnskapsførere.stream()
            .map(s -> s.getNavn())
            .map(this::navnToString)
            .collect(joining(","));
    }

    public String navnToString(Navn navn) {
        return (formatNavn(navn.getFornavn()) + " "
            + formatNavn(navn.getMellomnavn()) + " "
            + formatNavn(navn.getEtternavn()) + " ").trim();
    }

    public String formatNavn(String navn) {
        return Optional.ofNullable(navn).orElse("");
    }

    public String dato(LocalDate localDate) {
        return localDate != null ? localDate.format(DATE_FMT) : "?";
    }

    public String dato(List<LocalDate> dates) {
        return dates.stream()
            .map(d -> dato(d))
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

    private String getMessage(String key, MessageSource messages, Object... values) {
        return getMessage(key, null, messages, values);
    }

    private String getMessage(String key, String defaultValue, MessageSource messages, Object... values) {
        ((ResourceBundleMessageSource) messages).setDefaultEncoding("utf-8");
        return messages.getMessage(key, values, defaultValue, locale);
    }
}
