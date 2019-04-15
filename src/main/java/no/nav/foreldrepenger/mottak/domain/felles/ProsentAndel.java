package no.nav.foreldrepenger.mottak.domain.felles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.serialization.ProsentAndelDeserializer;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Prosent;

@Data 
@JsonDeserialize(using = ProsentAndelDeserializer.class)
public class ProsentAndel {

    @Prosent
    @JsonValue
    private final Double prosent;
    
    public ProsentAndel( Integer prosent) {
        this(prosent.doubleValue());  
        
    }

    @JsonCreator
    public ProsentAndel(@JsonProperty("prosent") Double prosent) {
        this.prosent = prosent;
    }
}
