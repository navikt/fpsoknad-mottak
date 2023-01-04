package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto;

public record OrganisasjonsNavnDTO(NavnDTO navn) {

    public String tilOrganisasjonsnavn() {
        var builder = new StringBuilder();
        leggTilNavnelinje(builder, navn.navnelinje1());
        leggTilNavnelinje(builder, navn.navnelinje2());
        leggTilNavnelinje(builder, navn.navnelinje3());
        leggTilNavnelinje(builder, navn.navnelinje4());
        leggTilNavnelinje(builder, navn.navnelinje5());
        return builder.toString();
    }

    private static void leggTilNavnelinje(StringBuilder stringBuilder, String navnelinje) {
        if (navnelinje != null && !navnelinje.isEmpty()) {
            if (!stringBuilder.isEmpty()) stringBuilder.append(", ");
            stringBuilder.append(navnelinje);
        }
    }
}
