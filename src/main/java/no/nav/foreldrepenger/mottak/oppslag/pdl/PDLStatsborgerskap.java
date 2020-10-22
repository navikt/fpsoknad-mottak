package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = ANY)
record PDLStatsborgerskap(@JsonProperty("land") String land) {

}