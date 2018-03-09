package no.nav.foreldrepenger.mottak.dokmot;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SkjemanummerTilDokumentTypeKodeNotUsed {

    private static final Logger logger = LoggerFactory.getLogger(SkjemanummerTilDokumentTypeKodeNotUsed.class);

    private static Map<String, String> map = initializeMap();

    private static Map<String, String> initializeMap() {
        HashMap<String, String> map = new HashMap<>();

        // Søknader

        map.put("NAV 14-05.06", "I000002"); // Søknad om foreldrepenger, mødrekvote eller fedrekvote ved adopsjon
        map.put("NAV 14-05.07", "I000003"); // Søknad om engangsstønad ved fødsel
        map.put("NAV 14-05.08", "I000004"); // Søknad om engangsstønad ved adopsjon
        map.put("NAV 14-05.09", "I000005"); // Søknad om foreldrepenger, mødrekvote eller fedrekvote ved fødsel
        map.put("NAV 14-05.10", "I000050"); // Søknad om endring av uttak av foreldrepenger eller overføring av kvote

        // Ettersendinger

        map.put("NAV 14-05.06-E", "I500002"); // Ettersending for foreldrepenger, mødrekvote eller fedrekvote ved
                                              // adopsjon
        map.put("NAV 14-05.07-E", "I500003"); // Ettersending for engangsstønad ved fødsel
        map.put("NAV 14-05.08-E", "I500004"); // Ettersending for engangsstønad ved adopsjon
        map.put("NAV 14-05.09-E", "I500005"); // Ettersending for foreldrepenger, mødrekvote eller fedrekvote ved fødsel
        map.put("NAV 14-05.10-E", "I500050"); // Ettersending for endring av uttak av foreldrepenger eller overføring av
                                              // kvote

        // Vedlegg
        map.put("H1", "I000006"); // Utsettelse eller gradert uttak av foreldrepenger (fleksibelt uttak)
        map.put("P3", "I000062"); // Bekreftelse på ventet fødselsdato
        map.put("R4", "I000063"); // Fødselsattest

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
