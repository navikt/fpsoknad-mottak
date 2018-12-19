package no.nav.foreldrepenger.mottak.util;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.mottak.http.errorhandling.UnsupportedVersionException;

public enum Versjon {

    V1("urn:no:nav:vedtak:felles:xml:soeknad:v1", "http://nav.no/foreldrepenger/soeknadsskjema/engangsstoenad/v1"), V2(
            "urn:no:nav:vedtak:felles:xml:soeknad:v2"), ALL;

    public static final String VERSION_PROPERTY = "contract.version";
    public final List<String> namespaces;

    Versjon() {
        this(Collections.emptyList());
    }

    Versjon(String... namespaces) {
        this(Arrays.asList(namespaces));
    }

    Versjon(List<String> namespaces) {
        this.namespaces = namespaces;
    }

    public static Versjon fraNamespace(String namespace) {
        return Arrays.stream(Versjon.values())
                .filter(v -> v.namespaces.contains(namespace))
                .findFirst()
                .orElseThrow(() -> new UnsupportedVersionException(namespace));
    }

    public static List<String> alleNamespaces() {
        return Arrays.stream(Versjon.values())
                .map(v -> v.namespaces)
                .flatMap(v -> v.stream())
                .collect(toList());
    }

    public static List<Versjon> alleVersjoner() {
        return Arrays.stream(values())
                .filter(v -> !ALL.equals(v))
                .collect(toList());
    }
}