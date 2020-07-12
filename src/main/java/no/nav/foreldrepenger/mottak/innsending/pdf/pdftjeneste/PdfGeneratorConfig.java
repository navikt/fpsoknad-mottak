package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "fppdfgen")
public class PdfGeneratorConfig {
    private static final String DEFAULT_URI = "http://fppdfgen";
    private static final String DEFAULT_BASE_PATH = "/api/v1/genpdf/";
    private static final String DEFAULT_PING_PATH = "is_alive";
    private static final String DEFAULT_SØKNAD_PATH = "soknad-v2/soknad";

    private final boolean enabled;
    private final URI pdfUri;
    private final URI pingUri;

    @ConstructorBinding
    public PdfGeneratorConfig(@DefaultValue(DEFAULT_URI) URI baseUri,
            @DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue(DEFAULT_BASE_PATH) String basePath,
            @DefaultValue(DEFAULT_SØKNAD_PATH) String søknadPath,
            @DefaultValue("true") boolean enabled) {
        this.enabled = enabled;
        this.pdfUri = uri(baseUri, basePath + søknadPath);
        this.pingUri = uri(baseUri, basePath + pingPath);
    }

    boolean isEnabled() {
        return enabled;
    }

    URI pdfUri() {
        return pdfUri;
    }

    URI pingUri() {
        return pingUri;
    }

    String name() {
        return pdfUri.getHost();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingUri=" + pingUri + ", enabled=" + enabled + ", pdfUri=" + pdfUri
                + "]";
    }

}
