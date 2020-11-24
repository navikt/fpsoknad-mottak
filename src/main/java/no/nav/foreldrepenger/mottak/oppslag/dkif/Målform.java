package no.nav.foreldrepenger.mottak.oppslag.dkif;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum Målform {

    @JsonEnumDefaultValue
    NB,
    NN,
    EN,
    E;

    private static final Logger LOG = LoggerFactory.getLogger(Målform.class);

    public static Målform standard() {
        LOG.trace("Bruker default målform NB");
        return NB;
    }
}
