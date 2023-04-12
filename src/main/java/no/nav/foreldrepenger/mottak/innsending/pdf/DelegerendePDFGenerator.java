package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.egenskaperFor;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.mapperFor;

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
    public byte[] generer(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return mapperFor(generatorer, egenskap).generer(søknad, søker, egenskap);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generatorer=" + generatorer + ", mapperEgenskaper=" + mapperEgenskaper
                + "]";
    }

}
