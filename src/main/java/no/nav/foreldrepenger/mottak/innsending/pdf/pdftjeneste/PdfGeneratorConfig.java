package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "fppdfgen")
public class PdfGeneratorConfig extends AbstractConfig {
    private static final String DEFAULT_URI = "http://fppdfgen";
    private static final String DEFAULT_BASE_PATH = "/api/v1/genpdf/";
    private static final String DEFAULT_PING_PATH = DEFAULT_BASE_PATH + "is_alive";
    private static final String DEFAULT_SØKNAD_PATH = DEFAULT_BASE_PATH + "soknad-v2/soknad";

    private final URI pdfUri;
    private final URI pingUri;

    @ConstructorBinding
    public PdfGeneratorConfig(@DefaultValue(DEFAULT_URI) URI baseUri,
            @DefaultValue(DEFAULT_PING_PATH) String pingPath,
            @DefaultValue(DEFAULT_SØKNAD_PATH) String soknadPath,
            @DefaultValue("true") boolean enabled) {
        super(baseUri, pingPath, enabled);

        this.pdfUri = uri(baseUri, soknadPath);
        this.pingUri = uri(baseUri, pingPath);
    }

    URI pdfUri() {
        return pdfUri;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingUri=" + pingUri + ", enabled=" + isEnabled() + ", pdfUri=" + pdfUri
                + "]";
    }

}
