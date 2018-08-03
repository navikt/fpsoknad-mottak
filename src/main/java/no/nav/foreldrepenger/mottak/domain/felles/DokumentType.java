package no.nav.foreldrepenger.mottak.domain.felles;

public enum DokumentType {
    SØKNAD_FORELDREPEMGER_ADOPSJON("I000002","Søknad om foreldrepenger ved adopsjon"),
    SØKNAD_ENGANGSSTØNAD_FØDSEL("I000003", "Søknad om engangsstønad ved fødsel"), 
    SØKNAD_FORELDREPEMGER_FØDSEL("I000005","Søknad om foreldrepenger ved fødsel"),
    UTSETTELSE_ELLER_GRADERT_UTTAK_FORELDREPENGER("I000006","Utsettelse eller gradert uttak av foreldrepenger (fleksibelt uttak)"),
    INNTEKTSOPPLYSNINGER_FRILANS_ELLER_SELVSTENDIG("I000007","Inntektsopplysninger om selvstendig næringsdrivende og/eller frilansere som skal ha foreldrepenger eller svangerskapspenger"),
    INNTEKTSOPPLYSNINGER_ARBEIDSTAKER("I000026","Inntektsopplysninger for arbeidstaker som skal ha sykepenger, foreldrepenger, svangerskapspenger, pleie-/opplæringspenger"),
    TERMINDATO_ELLER_OMSORGSOVERTAKELSESDATO("I000041","Dokumentasjon av termindato (lev. kun av mor), fødsel eller dato for omsorgsovertakelse"),
    OMSORGSOVERTAKELSESDATO("I000042","Dokumentasjon av overtakelse av omsorg"),
    ARBEIDSFORHOLD("I000043","Dokumentasjon av arbeidsforhold"),
    ETTERLØNN_ELLER_SLUTTVEDERLAG("I000044","Dokumentasjon av etterlønn eller sluttvederlag"),
    BRUKEROPPLASTET("I000047","Brukeropplastet dokumentasjon"),
    SØKNAD_ENDRING_UTTAK_ELLER_OVERFØRING_KVOTE("I000050","Søknad om endring av uttak av foreldrepenger eller overføring av kvote"),
    ANNET("I000060","Annet"),
    TERMINBEKREFTELSE("I000062", "Terminbekreftelse"),
    FØDSELSATTEST("I000063","Fødselsattest"),
    ETTERSENDELSE_FORELDREPENGER_ADOPSJON("I500002","Ettersendelse til søknad om foreldrepenger ved adopsjon"),
    ETTERSENDELSE_ENGANGSSTØNAD_FØDSEL("I500003","Ettersendelse til søknad om engangsstønad ved fødsel"),
    ETTERSENDELSE_ENGANGSSTØNAD_ADOPSJON("I500004","Ettersendelse til søknad om engangsstønad ved adopsjon"),
    ETTERSENDELSE_FORELDREPEMGER_FØDSEL("I500005","Ettersendelse til søknad om foreldrepenger ved fødsel"),
    ETTERSENDELSE_UTTAK_ELLER_OVERFØRING_KVOTE("I500006","Ettersendelse til utsettelse eller gradert uttak av foreldrepenger (fleksibelt uttak)"),
    ETTERSENDELSE_ENDRING_UTTAK_ELLER_OVERFØRING_KVOTE("I500050","Ettersendelse til søknad om endring av uttak av foreldrepenger eller overføring av kvote");


    public final String dokumentTypeId;
    public final String beskrivelse;

    DokumentType(String skjemaNummer) {
        this(skjemaNummer, null);
    }

    DokumentType(String skjemaNummer, String beskrivelse) {
        this.dokumentTypeId = skjemaNummer;
        this.beskrivelse = beskrivelse;
    }
}
