package no.nav.foreldrepenger.mottak.innsyn;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.foreldrepenger.mottak.util.jaxb.VedtakV2FPJAXBUtil;
import no.nav.vedtak.felles.xml.vedtak.uttak.fp.v2.UttakForeldrepenger;
import no.nav.vedtak.felles.xml.vedtak.uttak.fp.v2.UttaksresultatPeriode;
import no.nav.vedtak.felles.xml.vedtak.uttak.fp.v2.UttaksresultatPeriodeAktivitet;
import no.nav.vedtak.felles.xml.vedtak.v2.Uttak;

@Component
public class XMLV2VedtakMapper implements XMLVedtakMapper {

    private static final Logger LOG = LoggerFactory.getLogger(XMLV2VedtakMapper.class);

    private static final VedtakV2FPJAXBUtil JAXB = new VedtakV2FPJAXBUtil(true, true);

    @Override
    public List<Versjon> versjoner() {
        return Collections.singletonList(Versjon.V2);
    }

    @Override
    public Vedtak tilVedtak(String xml, Versjon v) {
        if (xml == null) {
            return null;
        }
        try {
            no.nav.vedtak.felles.xml.vedtak.v2.Vedtak vedtak = JAXB.unmarshal(xml,
                    no.nav.vedtak.felles.xml.vedtak.v2.Vedtak.class);
            Uttak uttak = vedtak.getBehandlingsresultat().getBeregningsresultat().getUttak();
            JAXBElement<UttakForeldrepenger> fp = (JAXBElement<UttakForeldrepenger>) uttak.getAny().get(0);
            UttakForeldrepenger u = fp.getValue();
            /*
             * for (FordelingPeriode p : u.getFordelingPerioder().getFordelingPeriode()) {
             * System.out.println( p.getPeriodetype().getKode() + ":" +
             * p.getPeriode().getFom() + "-" + p.getPeriode().getTom());
             * System.out.println("MOR : " + p.getMorsAktivitet().getKodeverk());
             * System.out.println("PERIODETYPE  : " + p.getPeriodetype().getKodeverk()); }
             * for (Stoenadskonto k : u.getStoenadskontoer()) { System.out.println("MAX : "
             * + k.getMaxdager().getValue()); System.out.println("TYPE: " +
             * k.getStoenadskontotype().getKode()); }
             */
            for (UttaksresultatPeriode urp : u.getUttaksresultatPerioder()) {
                System.out.println("---------");

                System.out.println("PERIODE " + urp.getPeriode().getFom() + "-" + urp.getPeriode().getTom());
                System.out.println("BEGRUNNELSE " + urp.getBegrunnelse().getValue());
                System.out.println("GRADERING " + urp.getGraderingInnvilget().isValue());
                System.out.println("MANUELL ÅRSAK " + urp.getManuellbehandlingaarsak().getKode());
                System.out.println("MANUELT BEHAANDLET " + urp.getManueltBehandlet().isValue());
                System.out.println("ÅRSAK " + urp.getPerioderesultataarsak().getKode());
                System.out.println("TYPE " + urp.getPeriodeResultatType().getKode());
                System.out.println("SAMTIDIG " + urp.getSamtidiguttak().isValue());
                System.out.println("UTSETTELSE " + urp.getUttakUtsettelseType().getKode());
                for (UttaksresultatPeriodeAktivitet a : urp.getUttaksresultatPeriodeAktiviteter()) {
                    System.out.println("\t---------");
                    System.out.println("\tID " + a.getArbeidsforholdid().getValue());
                    System.out.println("\tVIRKSOMHET " + a.getVirksomhet().getValue());
                }
            }
            return new Vedtak(xml);
        } catch (Exception e) {
            LOG.warn("Feil ved unmarshalling av vedtak", e);
            return null;

        }
    }

}
