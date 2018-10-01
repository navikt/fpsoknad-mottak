package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BehandlingWrapper {
    private final String status;
    private final String type;
    private final String tema;
    private final String årsak;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final List<Lenke> lenker;

    @JsonCreator
    public BehandlingWrapper(
            @JsonProperty("status") String status,
            @JsonProperty("type") String type,
            @JsonProperty("tema") String tema,
            @JsonProperty("årsak") String årsak,
            @JsonProperty("behandlendeEnhet") String behandlendeEnhet,
            @JsonProperty("behandlendeEnhetNavn") String behandlendeEnhetNavn,
            @JsonProperty("lenker") List<Lenke> lenker) {
        this.status = status;
        this.tema = tema;
        this.type = type;
        this.årsak = årsak;
        this.behandlendeEnhet = behandlendeEnhet;
        this.behandlendeEnhetNavn = behandlendeEnhetNavn;
        this.lenker = Optional.ofNullable(lenker).orElse(emptyList());
    }

    public Lenke getSøknadsLenke() {
        return getLenker().stream().filter(s -> s.getRel().equals("søknad")).findFirst().get();
    }
}
