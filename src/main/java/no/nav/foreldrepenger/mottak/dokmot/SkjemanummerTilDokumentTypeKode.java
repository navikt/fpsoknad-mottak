package no.nav.foreldrepenger.mottak.dokmot;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SkjemanummerTilDokumentTypeKode {

    private static final Logger logger = LoggerFactory.getLogger(SkjemanummerTilDokumentTypeKode.class);

    private static Map<String, String> map = initializeMap();

    private static Map<String, String> initializeMap() {
        HashMap<String, String> map = new HashMap<>();

        // Søknader
        map.put("NAV 10-07.40", "I000008"); // Søknad om stønad til anskaffelse av motorkjøretøy
        map.put("NAV 10-07.41", "I000010"); // Søknad om spesialutstyr og -tilpassing til bil

        map.put("NAV 14-05.06", "I000002"); // Søknad om foreldrepenger, mødrekvote eller fedrekvote ved adopsjon
        map.put("NAV 14-05.07", "I000003"); // Søknad om engangsstønad ved fødsel
        map.put("NAV 14-05.08", "I000004"); // Søknad om engangsstønad ved adopsjon
        map.put("NAV 14-05.09", "I000005"); // Søknad om foreldrepenger, mødrekvote eller fedrekvote ved fødsel
        map.put("NAV 14-05.10", "I000050"); // Søknad om endring av uttak av foreldrepenger eller overføring av kvote

        // Ettersendinger
        map.put("NAV 10-07.40-E", "I500008"); // Ettersending for stønad til anskaffelse av motorkjøretøy
        map.put("NAV 10-07.41-E", "I500010"); // Ettersending for spesialutstyr og -tilpassing til bil

        map.put("NAV 14-05.06-E", "I500002"); // Ettersending for foreldrepenger, mødrekvote eller fedrekvote ved
                                              // adopsjon
        map.put("NAV 14-05.07-E", "I500003"); // Ettersending for engangsstønad ved fødsel
        map.put("NAV 14-05.08-E", "I500004"); // Ettersending for engangsstønad ved adopsjon
        map.put("NAV 14-05.09-E", "I500005"); // Ettersending for foreldrepenger, mødrekvote eller fedrekvote ved fødsel
        map.put("NAV 14-05.10-E", "I500050"); // Ettersending for endring av uttak av foreldrepenger eller overføring av
                                              // kvote

        // Vedlegg
        map.put("L9", "I000023"); // Legeerklæring
        map.put("Z3", "I000045"); // Beskrivelse av funksjonsnedsettelse
        map.put("X3", "I000066"); // Kopi av likningsattest eller selvangivelse
        map.put("Z4", "I000022"); // Kopi av førerkort
        map.put("T7", "I000016"); // Dokumentasjon av inntekt
        map.put("Y9", "I000019"); // Dokumentasjon av veiforhold
        map.put("Z2", "I000021"); // Kopi av vognkort
        map.put("H1", "I000006"); // Utsettelse eller gradert uttak av foreldrepenger (fleksibelt uttak)
        map.put("L4", "I000033"); // Lønnsslipp
        map.put("N9", "I000037"); // Dokumentasjon av innleggelse i helseinstitusjon
        map.put("O5", "I000039"); // Dokumentasjon av militær- eller siviltjeneste
        map.put("P5", "I000042"); // Dokumentasjon av dato for overtakelse av omsorg
        map.put("T8", "I000043"); // Dokumentasjon av arbeidsforhold
        map.put("Y4", "I000044"); // Dokumentasjon av etterlønn/sluttvederlag
        map.put("K3", "I000051"); // Bekreftelse på deltakelse i kvalifiseringsprogrammet
        map.put("K4", "I000052"); // Inntektsopplysningsskjema
        map.put("K1", "I000058"); // Dokumentasjon av andre ytelser
        map.put("M6", "I000059"); // Timelister
        map.put("O9", "I000061"); // Bekreftelse fra studiested/skole
        map.put("P3", "I000062"); // Bekreftelse på ventet fødselsdato
        map.put("R4", "I000063"); // Fødselsattest
        map.put("T1", "I000064"); // Elevdokumentasjon fra lærested
        map.put("Z6", "I000065"); // Bekreftelse fra arbeidsgiver

        // Generelt
        map.put("L7", "I000046"); // Kvittering
        map.put("N6", "I000047"); // Annet

        return map;
    }

    static String dokumentTypeKode(String skjemanummer) {
        String dokumenttypeId = map.get(skjemanummer);
        if (dokumenttypeId == null) {
            logger.error("Fant ikke dokumenttypeid for skjemanummer {}", skjemanummer);
            throw new IllegalArgumentException("Fant ikke dokumenttypeid for skjemanummer " + skjemanummer);
        }
        return dokumenttypeId;
    }

}
