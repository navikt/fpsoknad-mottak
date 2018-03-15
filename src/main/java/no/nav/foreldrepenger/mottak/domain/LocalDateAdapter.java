package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;
import java.util.Optional;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return LocalDate.parse(v);
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return Optional.ofNullable(v).map(s -> s.toString()).orElse(null);
    }
}
