package no.nav.foreldrepenger.mottak.pdf;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ÅpenPeriode;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class SøknadTextFormatter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.uuuu");

    private final MessageSource landkoder;

    private final MessageSource kvitteringstekster;
    private final Locale locale;

    public SøknadTextFormatter(MessageSource landkoder, MessageSource kvitteringstekster, CountryCode countryCode) {
        this.landkoder = landkoder;
        this.kvitteringstekster = kvitteringstekster;
        this.locale = countryCode.toLocale();
    }

    public String countryName(String isoCode, Object... values) {
        return getMessage(isoCode, landkoder, values);
    }

    public String fromMessageSource(String key, Object... values) {
        return getMessage(key, kvitteringstekster, values);
    }

    public String navn(Navn navn) {
        String sammensattNavn = (Optional.ofNullable(navn.getFornavn()).orElse("") + " "
                + Optional.ofNullable(navn.getMellomnavn()).orElse("") + " "
                + Optional.ofNullable(navn.getEtternavn()).orElse("")).trim();
        return sammensattNavn.isEmpty() ? "" : fromMessageSource("navn", sammensattNavn);
    }

    public String date(LocalDate localDate) {
        return localDate != null ? localDate.format(DATE_FMT) : "?";
    }

    public String dates(List<LocalDate> dates) {
        return dates.stream()
                .map(this::date)
                .collect(joining(", "));
    }

    public String countryName(Boolean b) {
        return b ? "Norge" : "utlandet";
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
        StringBuilder sb = new StringBuilder("fom " + date(periode.getFom()));
        if (periode.getTom() != null) {
            sb.append(periode.getTom() != null ? " tom " + date(periode.getTom()) : " pågående");
        }
        return sb.toString();
    }

    public String capitalize(String orig) {
        String lowerWithSpace = orig.replaceAll("_", " ").toLowerCase();
        return lowerWithSpace.substring(0, 1).toUpperCase() + lowerWithSpace.substring(1);
    }

    public String vedlegg(Vedlegg vedlegg) {
        return Optional.ofNullable(vedlegg.getMetadata().getBeskrivelse())
                .orElse(vedlegg.getDokumentType().beskrivelse);
    }

    public List<String> utenlandsOpphold(List<Utenlandsopphold> opphold) {
        if (opphold.isEmpty()) {
            return Collections.singletonList(countryName(CountryCode.NO.getAlpha2()));
        }
        return opphold.stream()
                .map(this::formatOpphold)
                .collect(Collectors.toList());
    }

    private String formatOpphold(Utenlandsopphold opphold) {
        return countryName(opphold.getLand().getAlpha2(), opphold.getLand().getName())
                + ": "
                + date(opphold.getVarighet().getFom()) + " - "
                + date(opphold.getVarighet().getTom());
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
