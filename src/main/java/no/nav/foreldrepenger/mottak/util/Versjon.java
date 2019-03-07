package no.nav.foreldrepenger.mottak.util;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public enum Versjon {

    V1("urn:no:nav:vedtak:felles:xml:soeknad:v1",
       "urn:no:nav:vedtak:felles:xml:vedtak:v1",
       "urn:no:nav:vedtak:felles:xml:soeknad:svangerskapspenger:v1",
       "http://nav.no/foreldrepenger/soeknadsskjema/engangsstoenad/v1"),
    V2("urn:no:nav:vedtak:felles:xml:vedtak:v2",
       "urn:no:nav:vedtak:felles:xml:soeknad:engangsstoenad:v2",
       "urn:no:nav:vedtak:felles:xml:soeknad:v2",
       "urn:no:nav:vedtak:felles:xml:soeknad:endringssoeknad:v2"),
    V3("urn:no:nav:vedtak:felles:xml:soeknad:v3",
       "urn:no:nav:vedtak:felles:xml:soeknad:endringssoeknad:v3"),
    V20180924,
    UKJENT;

    private static final Logger LOG = LoggerFactory.getLogger(Versjon.class);
    public static final String VERSION_PROPERTY = "contract.version";
    private final List<String> namespaces;

    public static final List<Versjon> UKJENT_VERSJON = singletonList(UKJENT);

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
                .orElse(ukjent(namespace));
    }

    private static Versjon ukjent(String namespace) {
        LOG.warn("Fant ingen versjon for namespace {}", namespace);
        return UKJENT;
    }

    public static List<String> alleNamespaces() {
        return stream(values())
                .map(v -> v.namespaces)
                .flatMap(v -> v.stream())
                .collect(toList());
    }
}