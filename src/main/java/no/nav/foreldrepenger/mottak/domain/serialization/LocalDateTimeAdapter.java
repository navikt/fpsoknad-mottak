package no.nav.foreldrepenger.mottak.domain.serialization;

import static java.time.format.DateTimeFormatter.ISO_DATE;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    @Override
    public LocalDateTime unmarshal(String dato) throws Exception {
        return LocalDateTime.parse(dato, ISO_DATE);
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return Optional.ofNullable(v).map(s -> s.toString()).orElse(null);
    }
}
