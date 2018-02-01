package no.nav.foreldrepenger.mottak.domain;

import java.io.IOException;

import org.springframework.core.io.Resource;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class PåkrevdVedlegg extends Vedlegg {

    public PåkrevdVedlegg(Resource vedlegg) throws IOException {
        super(vedlegg);
    }
}
