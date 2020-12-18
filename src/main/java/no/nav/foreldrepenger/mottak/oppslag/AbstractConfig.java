package no.nav.foreldrepenger.mottak.oppslag;

import java.net.URI;

import javax.inject.Inject;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.util.TokenUtil;
import no.nav.foreldrepenger.mottak.util.URIUtil;

public class AbstractConfig {
    private final URI baseUri;
    private final String pingPath;
    private final boolean enabled;

    @Inject
    private TokenUtil tokenUtil;

    public AbstractConfig(URI baseUri, String pingPath, boolean enabled) {
        this.baseUri = baseUri;
        this.pingPath = pingPath;
        this.enabled = enabled;
    }

    public TokenUtil getTokenUtil() {
        return tokenUtil;
    }

    public void setTokenUtil(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    public URI pingEndpoint() {
        return URIUtil.uri(baseUri, pingPath);
    }

    public String getPingPath() {
        return pingPath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public URI getBaseUri() {
        return baseUri;
    }

    public String name() {
        return baseUri.getHost();
    }

    public String getSubject() {
        return tokenUtil.getSubject();
    }

    public Fødselsnummer fnr() {
        return tokenUtil.fnr();
    }
}
