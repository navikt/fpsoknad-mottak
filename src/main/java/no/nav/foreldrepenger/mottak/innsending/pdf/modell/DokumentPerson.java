package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

public record DokumentPerson(String id,
                             String typeId,
                             String navn,
                             PersonType type,
                             String bosattLand,
                             String nasjonalitet) {

    public static final DokumentPerson UKJENT = new DokumentPerson(null, null, null, PersonType.UKJENT, null, null);
}
