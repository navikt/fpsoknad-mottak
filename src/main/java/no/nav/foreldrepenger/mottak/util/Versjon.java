package no.nav.foreldrepenger.mottak.util;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.foreldrepenger.mottak.domain.FagsakType.SVANGERSKAPSPENGER;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.innsending.SøknadType;

public enum Versjon {
    V1("urn:no:nav:vedtak:felles:xml:soeknad:v1", "urn:no:nav:vedtak:felles:xml:soeknad:foreldrepenger:v1", "urn:no:nav:vedtak:felles:xml:soeknad:endringssoeknad:v1", "urn:no:nav:vedtak:felles:xml:vedtak:v1", "urn:no:nav:vedtak:felles:xml:soeknad:svangerskapspenger:v1", "urn:no:nav:vedtak:felles:xml:soeknad:engangsstoenad:v1", "http://nav.no/foreldrepenger/soeknadsskjema/engangsstoenad/v1"),
    V2("urn:no:nav:vedtak:felles:xml:soeknad:v2", "urn:no:nav:vedtak:felles:xml:soeknad:foreldrepenger:v2", "urn:no:nav:vedtak:felles:xml:vedtak:v2", "urn:no:nav:vedtak:felles:xml:soeknad:engangsstoenad:v2", "urn:no:nav:vedtak:felles:xml:soeknad:endringssoeknad:v2"),
    V3("urn:no:nav:vedtak:felles:xml:soeknad:foreldrepenger:v3", "urn:no:nav:vedtak:felles:xml:soeknad:engangsstoenad:v3", "urn:no:nav:vedtak:felles:xml:soeknad:endringssoeknad:v3"),
    V20180924,
    UKJENT;

    private static final Logger LOG = LoggerFactory.getLogger(Versjon.class);
    public static final String VERSION_PROPERTY = "contract.version";
    private final List<String> namespaces;
    public static final List<Versjon> UKJENT_VERSJON = singletonList(UKJENT);
    public static final Versjon DEFAULT_VERSJON = V3;
    public static final Versjon DEFAULT_SVP_VERSJON = V1;

    private Versjon() {
        this(emptyList());
    }

    private Versjon(String... namespaces) {
        this(asList(namespaces));
    }

    private Versjon(List<String> namespaces) {
        this.namespaces = namespaces;
    }

    public static Versjon defaultVersjon(SøknadType type) {
        return SVANGERSKAPSPENGER.equals(type.fagsakType()) ? DEFAULT_SVP_VERSJON : DEFAULT_VERSJON;
    }

    public static boolean erEngangsstønadV1Dokmot(String namespace) {
        return startsWith(namespace, "http");
    }

    private static boolean startsWith(String namespace, String prefix) {
        return Optional.ofNullable(namespace)
                .filter(ns -> ns.startsWith(prefix))
                .isPresent();
    }

    public static Versjon namespaceFra(String ns) {
        return Arrays.stream(values())
                .filter(v -> v.namespaces.contains(ns))
                .findFirst()
                .orElse(ukjent(ns));
    }

    private static Versjon ukjent(String namespace) {
        LOG.warn("Fant ingen versjon for namespace {} blant {}", namespace, allNamespaces());
        return UKJENT;
    }

    private static List<String> allNamespaces() {
        return stream(values())
                .map(e -> e.namespaces)
                .flatMap(List::stream)
                .toList();
    }

    public boolean erUkjent() {
        return UKJENT.equals(this);
    }
}