package no.nav.foreldrepenger.mottak.util;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import no.nav.foreldrepenger.mottak.http.errorhandling.UnsupportedVersionException;

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
        return Arrays.stream(Versjon.values())
                .filter(v -> namespace.equals(v.namespace))
                .findFirst()
                .orElseThrow(() -> new UnsupportedVersionException(namespace));
    }

    public static List<String> namespaces() {
        return Arrays.stream(Versjon.values())
                .filter(s -> s.namespace != null)
                .map(s -> s.namespace)
                .collect(toList());
    }

    public static List<Versjon> concreteValues() {
        return Arrays.stream(values())
                .filter(v -> !ALL.equals(v))
                .collect(toList());
    }
}