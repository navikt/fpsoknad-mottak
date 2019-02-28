package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.util.StringUtil.limit;

import lombok.Data;

@Data
public class Vedtak {
    private final String xml;

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [xml=" + limit(xml, 200) + "]";
    }
}
