package no.nav.foreldrepenger.mottak.domain.felles;

import static java.util.Arrays.asList;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Valid
public final class Ettersending {

    @NotNull
    private final String saksnr;
    @NotNull
    private final EttersendingsType type;
    private final List<Vedlegg> vedlegg;
    private String dialogId;

    public Ettersending(EttersendingsType type, String saksnr, Vedlegg... vedlegg) {
        this(type, saksnr, asList(vedlegg));
    }

    @JsonCreator
    public Ettersending(@JsonProperty("type") EttersendingsType type,
            @JsonProperty("saksnr") String saksnr,
            @JsonProperty("vedlegg") List<Vedlegg> vedlegg) {
        this.type = type;
        this.saksnr = saksnr;
        this.vedlegg = vedlegg;
    }
}
