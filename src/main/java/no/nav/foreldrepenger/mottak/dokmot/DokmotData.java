package no.nav.foreldrepenger.mottak.dokmot;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import no.nav.foreldrepenger.mottak.domain.LocalDateTimeAdapter;

@XmlRootElement
public class DokmotData {

    private static final Map<String, Filtype> MIMETYPE_TIL_FILTYPE;
    static {
        MIMETYPE_TIL_FILTYPE = new HashMap<>();
        MIMETYPE_TIL_FILTYPE.put("application/xml", Filtype.XML);
        MIMETYPE_TIL_FILTYPE.put("application/pdf", Filtype.PDF);
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime innsendtDato;
    public String fodselsnummer;
    public String aktorId;
    public HovedskjemaData hovedskjema;
    public List<VedleggData> vedleggListe;
    public List<AlternativRepresentasjon> alternativRepresentasjonListe;
    public String henvendelsetype;
    public String tema;
    public String behandlingstema;
    public String behandlingsId;

    public static Filtype mimeTypeTilFiltype(String mimeType) {
        return MIMETYPE_TIL_FILTYPE.get(mimeType);
    }

    public abstract static class DokumentInfo {
        public String skjemanummer;
        public Filtype filtype;
        public String filstorrelse;
        public Variant variant;
    }

    @XmlRootElement
    public static class HovedskjemaData extends DokumentInfo {

        public HovedskjemaData() {
            filtype = Filtype.PDFA;
            variant = Variant.ARKIV;
        }

        public HovedskjemaData(String skjemanummer, String filstorrelse) {
            this();
            this.skjemanummer = skjemanummer;
            this.filstorrelse = filstorrelse;
        }
    }

    @XmlRootElement
    public static class VedleggData extends DokumentInfo {
        public String brukerTittel;

        public VedleggData() {
        }

        public VedleggData(String skjemanummer, Filtype filtype, String filstorrelse) {
            this.skjemanummer = skjemanummer;
            this.filtype = filtype;
            this.filstorrelse = filstorrelse;
            this.variant = Variant.ARKIV;
        }

    }

    @XmlRootElement
    @XmlType(name = "alternativRepresentasjonDokmot")
    public static class AlternativRepresentasjon extends DokumentInfo {
        public String filnavn;

        public AlternativRepresentasjon(String filnavn, Filtype type) {
            this.filnavn = filnavn;
            this.filtype = type;
            this.variant = Variant.ORIGINAL;
        }

        @SuppressWarnings("UnusedDeclaration")
        public AlternativRepresentasjon() {
        }

    }

    public enum Filtype {
        PDF, PDFA, XML
    }

    public enum Variant {
        ARKIV, ORIGINAL
    }

}
