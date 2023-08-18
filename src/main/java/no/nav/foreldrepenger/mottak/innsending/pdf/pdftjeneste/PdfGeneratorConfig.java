package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "fppdfgen")
public class PdfGeneratorConfig {
    private static final String DEFAULT_URI = "http://fppdfgen";
    private static final String DEFAULT_BASE_PATH = "/api/v1/genpdf/";
    private static final String DEFAULT_SØKNAD_PATH = DEFAULT_BASE_PATH + "soknad-v2/soknad";

    private final URI pdfUri;
    private final boolean enabled;
    private final URI baseUri;

    public PdfGeneratorConfig(@DefaultValue(DEFAULT_URI) URI baseUri,
            @DefaultValue(DEFAULT_SØKNAD_PATH) String soknadPath,
            @DefaultValue("true") boolean enabled) {
        this.baseUri = baseUri;
        this.enabled = enabled;
        this.pdfUri = uri(baseUri, soknadPath);
    }

    URI pdfUri() {
        return pdfUri;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", pdfUri=" + pdfUri
                + "]";
    }

    public URI getBaseUri() {
        return baseUri;
    }
}
