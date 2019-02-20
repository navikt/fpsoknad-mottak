package no.nav.foreldrepenger.mottak.domain.felles;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Valid
public class Ettersending {

    private final String saksnr;
    private final EttersendingsType type;
    private final List<Vedlegg> vedlegg;

    public Ettersending(EttersendingsType type, String saksnr, Vedlegg... vedlegg) {
        this(type, saksnr, Arrays.asList(vedlegg));
    }

    @JsonCreator
    public Ettersending(@JsonProperty("type") @NotNull EttersendingsType type,
            @JsonProperty("saksnr") String saksnr,
            @JsonProperty("vedlegg") List<Vedlegg> vedlegg) {
        this.type = type;
        this.saksnr = saksnr;
        this.vedlegg = vedlegg;
    }
}
