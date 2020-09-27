package no.nav.foreldrepenger.mottak.domain.felles;

public enum DokumentType {

    I000001("Søknad om svangerskapspenger"),
    I000002("Søknad om foreldrepenger ved adopsjon"),
    I000003("Søknad om engangsstønad ved fødsel"),
    I000004("Søknad om engangsstønad ved adopsjon"),
    I000005("Søknad om foreldrepenger ved fødsel"),
    I000006("Utsettelse eller gradert uttak av foreldrepenger (fleksibelt uttak)"),
    I000007("Inntektsopplysninger om selvstendig næringsdrivende og/eller frilansere som skal ha foreldrepenger eller svangerskapspenger"),
    I000023("Legeerklæring"),
    I000026("Inntektsopplysninger for arbeidstaker som skal ha sykepenger, foreldrepenger, svangerskapspenger, pleie-/opplæringspenger"),
    I000032("Resultatregnskap"),
    I000036("Bekreftelse på avtalt ferie"),
    I000037("Mor er innlagt i helseinstitusjon"),
    I000038("Mor er i arbeid, tar utdanning eller er for syk til å ta seg av barnet"),
    I000039("Tjenestebevis"),
    I000041("Dokumentasjon av termindato, fødsel eller dato for omsorgsovertakelse"),
    I000042("Dokumentasjon av overtakelse av omsorg"),
    I000043("Dokumentasjon av arbeidsforhold"),
    I000044("Dokumentasjon av etterlønn eller sluttvederlag"),
    I000045("Beskrivelse/Dokumentasjon funksjonsnedsettelse"),
    I000047("Brukeropplastet dokumentasjon"),
    I000049("Annet skjema (ikke NAV-skjema)"),
    I000050("Søknad om endring av uttak av foreldrepenger eller overføring av kvote"),
    I000051("Mor deltar i kvalifiseringsprogrammet"),
    I000060("Annet"),
    I000061("Mor tar utdanning på heltid"),
    I000062("Terminbekreftelse"),
    I000063("Fødselsattest"),
    I000065("Bekreftelse fra arbeidsgiver"),
    I000066("Kopi av skattemelding"),
    I000107("Vurdering av arbeidsmulighet/sykmelding"),
    I000108("Opplysninger om muligheter og behov for tilrettelegging ved svangerskap"),
    I000109("Skjema for tilrettelegging og omplassering ved graviditet"),
    I000110("Dokumentasjon av aleneomsorg"),
    I000111("Dokumentasjon av begrunnelse for hvorfor man søker tilbake i tid"),
    I000112("Dokumentasjon av deltakelse i introduksjonsprogrammet"),
    I000114("Svar på varsel om tilbakebetaling"),
    I000116("Bekreftelse på øvelse eller tjeneste i Forsvaret eller Sivilforsvaret"),
    I000117("Bekreftelse på tiltak i regi av Arbeids- og velferdsetaten"),
    I500002("Ettersendelse til søknad om foreldrepenger ved adopsjon"),
    I500003("Ettersendelse til søknad om engangsstønad ved fødsel"),
    I500004("Ettersendelse til søknad om engangsstønad ved adopsjon"),
    I500005("Ettersendelse til søknad om foreldrepenger ved fødsel"),
    I500006("Ettersendelse til utsettelse eller gradert uttak av foreldrepenger (fleksibelt uttak)"),
    I500050("Ettersendelse til søknad om endring av uttak av foreldrepenger eller overføring av kvote");

    private final String beskrivelse;

    DokumentType(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }
}
