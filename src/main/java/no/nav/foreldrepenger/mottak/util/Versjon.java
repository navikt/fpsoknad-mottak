package no.nav.foreldrepenger.mottak.util;

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
}