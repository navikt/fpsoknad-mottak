package no.nav.foreldrepenger.mottak.domain.felles;

public enum DokumentType {
    I000002("Søknad om foreldrepenger ved adopsjon"),
    I000003("Søknad om engangsstønad ved fødsel"), 
    I000005("Søknad om foreldrepenger ved fødsel"),
    I000006("Utsettelse eller gradert uttak av foreldrepenger (fleksibelt uttak)"),
    I000007("Inntektsopplysninger om selvstendig næringsdrivende og/eller frilansere som skal ha foreldrepenger eller svangerskapspenger"),
    I000026("Inntektsopplysninger for arbeidstaker som skal ha sykepenger, foreldrepenger, svangerskapspenger, pleie-/opplæringspenger"),
    I000041("Dokumentasjon av termindato (lev. kun av mor), fødsel eller dato for omsorgsovertakelse"),
    I000042("Dokumentasjon av overtakelse av omsorg"),
    I000043("Dokumentasjon av arbeidsforhold"),
    I000044("Dokumentasjon av etterlønn eller sluttvederlag"),
    I000047("Brukeropplastet dokumentasjon"),
    I000050("Søknad om endring av uttak av foreldrepenger eller overføring av kvote"),
    I000060("Annet"),
    I000062("Terminbekreftelse"),
    I000063("Fødselsattest"),
    I500002("Ettersendelse til søknad om foreldrepenger ved adopsjon"),
    I500003("Ettersendelse til søknad om engangsstønad ved fødsel"),
    I500004("Ettersendelse til søknad om engangsstønad ved adopsjon"),
    I500005("Ettersendelse til søknad om foreldrepenger ved fødsel"),
    I500006("Ettersendelse til utsettelse eller gradert uttak av foreldrepenger (fleksibelt uttak)"),
    I500050("Ettersendelse til søknad om endring av uttak av foreldrepenger eller overføring av kvote");


    public final String beskrivelse;  

    DokumentType(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
