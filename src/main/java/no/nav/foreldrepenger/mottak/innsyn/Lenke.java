package no.nav.foreldrepenger.mottak.innsyn;

import java.util.Objects;

public class Lenke {

    private final String href;
    private final String rel;
    private final String type;

    public Lenke(String href, String rel, String type) {
        this.href = href;
        this.rel = rel;
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public String getRel() {
        return rel;
    }

    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, rel, href);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Lenke other = (Lenke) obj;
        if (href == null) {
            if (other.href != null)
                return false;
        }
        else if (!href.equals(other.href))
            return false;
        if (rel == null) {
            if (other.rel != null)
                return false;
        }
        else if (!rel.equals(other.rel))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        }
        else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [href=" + href + ", rel=" + rel + ", type=" + type + "]";
    }
}
