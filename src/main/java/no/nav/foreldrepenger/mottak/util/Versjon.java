package no.nav.foreldrepenger.mottak.util;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public enum Versjon {

    V1("urn:no:nav:vedtak:felles:xml:soeknad:v1"), V2("urn:no:nav:vedtak:felles:xml:soeknad:v2"), ALL;

    public static final String VERSION_PROPERTY = "contract.version";
    public String namespace;

    Versjon() {
        this(null);
    }

    Versjon(String namespace) {
        this.namespace = namespace;
    }

    public static Versjon fraNamespace(String namespace) {
        for (Versjon v : Versjon.values()) {
            if (namespace.equals(v.namespace)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Fant ingen versjon for  namespace " + namespace);
    }

    public static List<Versjon> concreteValues() {
        return Arrays.stream(values())
                .filter(v -> !ALL.equals(v))
                .collect(toList());
    }
}