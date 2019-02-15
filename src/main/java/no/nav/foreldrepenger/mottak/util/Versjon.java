package no.nav.foreldrepenger.mottak.util;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;

public enum Versjon {

    V1("urn:no:nav:vedtak:felles:xml:soeknad:v1", "http://nav.no/foreldrepenger/soeknadsskjema/engangsstoenad/v1"),
    V2("urn:no:nav:vedtak:felles:xml:soeknad:v2","urn:no:nav:vedtak:felles:xml:soeknad:endringssoeknad:v2"), 
    V3("urn:no:nav:vedtak:felles:xml:soeknad:v3","urn:no:nav:vedtak:felles:xml:soeknad:endringssoeknad:v3"), 
    V20180924, UKJENT;

    public static final String VERSION_PROPERTY = "contract.version";
    private final List<String> namespaces;

    public static final Versjon DEFAULT_VERSJON = V2;

    private Versjon() {
        this(emptyList());
    }

    private Versjon(String... namespaces) {
        this(asList(namespaces));
    }

    private Versjon(List<String> namespaces) {
        this.namespaces = namespaces;
    }

    public static Versjon namespaceFra(String namespace) {
        return stream(values())
                .filter(v -> v.namespaces.contains(namespace))
                .findFirst()
                .orElse(Versjon.UKJENT);
    }

    public static List<String> alleNamespaces() {
        return stream(values())
                .map(v -> v.namespaces)
                .flatMap(v -> v.stream())
                .collect(toList());
    }

    public static List<Versjon> alleSøknadVersjoner() {
        return stream(values())
                .filter(v -> !UKJENT.equals(v))
                .filter(v -> !V20180924.equals(v))
                .collect(toList());
    }
}