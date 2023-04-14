package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.egenskaperFor;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.mapperFor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;

@Component
@Qualifier(DELEGERENDE)
public class DelegerendePDFGenerator implements MappablePdfGenerator {

    private final List<MappablePdfGenerator> generatorer;
    private final MapperEgenskaper mapperEgenskaper;

    public DelegerendePDFGenerator(MappablePdfGenerator... generatorer) {
        this(List.of(generatorer));
    }

    @Autowired
    public DelegerendePDFGenerator(List<MappablePdfGenerator> generatorer) {
        this.generatorer = generatorer;
        this.mapperEgenskaper = egenskaperFor(generatorer);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return mapperEgenskaper;
    }

    @Override
    public byte[] generer(Søknad søknad, SøknadEgenskap egenskap, InnsendingPersonInfo person) {
        return mapperFor(generatorer, egenskap).generer(søknad, egenskap, person);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generatorer=" + generatorer + ", mapperEgenskaper=" + mapperEgenskaper
                + "]";
    }

}
